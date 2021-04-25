package io.terminus.dalaran.core.resource.log;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class RequestID {

    public static String getExceptionStackTrace(Throwable anexcepObj) {
        StringWriter sw = null;
        PrintWriter printWriter = null;
        try {
            if(anexcepObj != null) {
                sw = new StringWriter();
                printWriter = new PrintWriter(sw);
                anexcepObj.printStackTrace(printWriter);
                printWriter.flush();
                sw.flush();
                return sw.toString();
            } else {
                return null;
            }
        } finally {
            try {
                if (sw != null) {
                    sw.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                log.error(String.format("Reason: %s, ExceptionStackTrace: %s", "Failed to ClosIO",
                        e.getMessage()));
            }
        }
    }
}
