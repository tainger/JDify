package io.terminus.dalaran.console.filter;

import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "CORSFilter", urlPatterns = {"/*"})
public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse)response;
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals("OPTIONS")) {
            resp.setStatus(200);
            if (UserContext.getUserInfo() != null) {
                resp.setHeader(HttpHeaders.SET_COOKIE, StringUtils.substring(UserContext.getCookies().toString(), 1, UserContext.getCookies().toString().length() - 1));
            }
            resp.flushBuffer();
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
