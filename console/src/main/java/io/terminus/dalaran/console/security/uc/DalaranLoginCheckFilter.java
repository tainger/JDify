package io.terminus.dalaran.console.security.uc;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.model.UserContext;
import io.terminus.dalaran.model.security.CustomResponseBody;
import io.terminus.dalaran.model.security.SecurityType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@WebFilter(filterName = "DalaranLoginCheckFilter", urlPatterns = {"/*"})
public class DalaranLoginCheckFilter implements Filter {

    private static final int ERROR_CODE  = 403;

    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/v2/api-docs", "/swagger-resources/configuration/ui", "/swagger-resources", "/swagger-resources/configuration/security",
                    "/swagger-ui.html", "/webjars", "/actuator")));

    @Value(value = "${draco.filter}")
    private boolean filter;

    @Value(value = "${draco.login}")
    private String loginUrl;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");

        if (filter && UserContext.getUserInfo() == null && !containsPath(ALLOWED_PATHS, path)) {
            CustomResponseBody responseBody = new CustomResponseBody();
            responseBody.setType(SecurityType.UC);
            responseBody.setMessage(loginUrl);
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setCharacterEncoding("UTF-8");
            response.setStatus(ERROR_CODE);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.println(JSON.toJSON(responseBody));
            writer.flush();
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    private boolean containsPath(Set<String> paths, String request) {
        for (String path : paths) {
            if (StringUtils.startsWithIgnoreCase(request, path)) {
                return true;
            }
        }
        return false;
    }
}
