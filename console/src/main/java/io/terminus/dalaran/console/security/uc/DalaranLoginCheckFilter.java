package io.terminus.dalaran.console.security.uc;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.model.security.CustomResponseBody;
import io.terminus.dalaran.model.security.SecurityType;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@WebFilter(filterName = "DalaranLoginCheckFilter", urlPatterns = {"/v2/api-docs", "/swagger-resources/configuration/ui", "/swagger-resources", "/swagger-resources/configuration/security",
        "/swagger-ui.html", "/webjars/**", "/actuator/**"})
public class DalaranLoginCheckFilter implements Filter {

    private static final int ERROR_CODE  = 403;

    @Value(value = "${draco.filter}")
    private boolean filter;

    @Value(value = "${draco.login}")
    private String loginUrl;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (filter && UserContext.getUserInfo() == null) {
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
}
