package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.market.MarketResourceLoader;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Component
public class DefaultMarketResourceLoader implements MarketResourceLoader {

    @Autowired
    private DalaranComponentContext componentContext;

    @Override
    public void loadProcessor(File file, String type, String version) {
        try {
            ClassLoader classLoader = loadJars(file);
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntity = entries.nextElement();
                String name = jarEntity.getName();
                if (jarEntity.isDirectory() || !name.endsWith(".class")) {
                    continue;
                }
                String className = name.replace('/', '.');
                className = className.substring(0, className.length() - 6);
                Class clazz = Class.forName(className, true, classLoader);
                Annotation annotation = clazz.getAnnotation(Processor.class);
                if (annotation != null) {
                    componentContext.addProcessor((DalaranProcessor)clazz.newInstance(), type, version);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClassLoader loadJars(File file) throws Exception {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        File jarDir = new File("dalaran-runner-jar-dir", "tmp");
        jarDir.mkdirs();
        Set<File> filterJar = new HashSet<>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory() && jarEntry.getName().startsWith("lib/") && jarEntry.getName().endsWith(".jar")) {
                String jarEntryName = jarEntry.getName().substring(4);
                File dependJarFile = new File(jarDir, jarEntryName);
                dependJarFile.createNewFile();
                FileUtils.copyToFile(jarFile.getInputStream(jarEntry), dependJarFile);
                filterJar.add(dependJarFile);
            }
        }
        filterJar.add(file);
        URL[] urls = filterJar.stream().map(it -> {
            try {
                return it.toURL();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).toArray(URL[]::new);
        return new URLClassLoader(urls, Processor.class.getClassLoader());
    }
}
