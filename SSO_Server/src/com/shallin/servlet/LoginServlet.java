package com.shallin.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * TODO {file desc}
 */
public class LoginServlet extends HttpServlet {

    private String domains;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("aaaaa");
        domains = config.getInitParameter("domains");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        System.out.println("code"+code);
        String sessionCode = (String)request.getSession().getAttribute("kcode");
        if (Objects.equals("/login", request.getServletPath())) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String source = request.getParameter("source");

            if (null == source || Objects.equals("", source)) {
                String referer = request.getHeader("referer");
                source = referer.substring(referer.indexOf("source=") + 7);
            }

            if (Objects.equals(username, password)) {
                String ticket = UUID.randomUUID().toString().replace("-", "");
                System.out.println("******************************:" + ticket);
                response.sendRedirect(source + "/main?ticket=" + ticket + "&domains=" +
                        domains.replace(source + ",", "").replace("," + source, "").replace(source, ""));
            } else {
                request.setAttribute("source", source);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } else if (Objects.equals("/ssoLogin", request.getServletPath())) {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else if (Objects.equals("/ssoLogout", request.getServletPath())) {
            String source = request.getParameter("source");

            //由于用户信息没有存储在内存或者类似memcache这样的缓存中，所以这里没有相关的助理
            //在ssoLogout请求时，传过来当前的用户名，根据用户名查找内存或者缓存，删除相应信息，以完成退出
            //用户从哪来？在实行ssoLogin时返回的ticket中，要包含用户的信息（能标识用户唯一性即可，uuid也可以，只是需要在sso的server中记录一下这个uuid和用户的对应关系）
            //webapp1或者webapp2在调用ssoLogout时把ticket传回来即可

            if (null == source || Objects.equals("", source)) {
                String referer = request.getHeader("referer");
                source = referer.substring(referer.indexOf("source=") + 7);
            }

            response.sendRedirect(source + "/logout?domains=" +
                    domains.replace(source + ",", "").replace("," + source, "").replace(source, ""));
        }

    }
}
