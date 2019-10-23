package com.shallin.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * TODO {file desc}
 */
public class UserFilter implements Filter {

    private String server;

    private String app;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        server = filterConfig.getInitParameter("server");
        app = filterConfig.getInitParameter("app");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (Objects.equals("/ssoLogout", ((HttpServletRequest)request).getServletPath())) {
            ((HttpServletResponse) response).sendRedirect(server + "/ssoLogout?source=" + app);
            return;
        }

        String ticket = null;
        if (null != ((HttpServletRequest)request).getCookies()) {
            for (Cookie cookie : ((HttpServletRequest)request).getCookies()) {
                if (Objects.equals(cookie.getName(), "Ticket_Granting_Ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (!Objects.equals(null, ticket)) {
            //判断超时时间
            String[] values = ticket.split(":");
            ticket = request.getParameter("ticket");
            if (Long.valueOf(values[1]) < System.currentTimeMillis()) {//超时
                if (Objects.equals(null, ticket)) {
                    ((HttpServletResponse) response).sendRedirect(server + "/ssoLogin?source=" + app);
                    return;
                } else {
                    ticket = ticket + ":" + (System.currentTimeMillis() + 10000);
                    ((HttpServletResponse)response).addCookie(new Cookie("Ticket_Granting_Ticket", ticket));
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            //应该进行用户校验，如果不是用户或用户非法，需要跳转到登录页面或者不需要登录的页面
            filterChain.doFilter(request, response);
            return;
        }

        ticket = request.getParameter("ticket");
        if (!Objects.equals(null, ticket) && !Objects.equals("", ticket.trim())) {
            ticket = ticket + ":" + (System.currentTimeMillis() + 10000);
            ((HttpServletResponse)response).addCookie(new Cookie("Ticket_Granting_Ticket", ticket));
            filterChain.doFilter(request, response);
        } else {
            ((HttpServletResponse)response).sendRedirect(server + "/ssoLogin?source=" + app);
        }
    }

    @Override
    public void destroy() {

    }
}
