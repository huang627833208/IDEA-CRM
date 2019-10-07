package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 作者：杜聚宾
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("进入到判断有没有登录过的过滤器");

        /*

            从request对象中取得session对象
            从session对象中取得user对象
            判断user对象，如果为null，说明没登录过，不为null，说明登录过

         */

        /*

            ServletRequest  父 有
            HttpServletRequest 子 用

         */

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //取得访问路径
        String path = request.getServletPath();

        //如果是与登录相关的资源路径，则自动放行
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){

            chain.doFilter(req, resp);


        //其他资源，必须验证有没有登录过
        }else{

            User user = (User)request.getSession().getAttribute("user");

            //user不为null说明登录过
            if(user!=null){

                //将请求放行到目标资源
                chain.doFilter(req, resp);

                //user为null说明没登录过
            }else{

                //将请求重定向到登录页
            /*

                我们在后台使用路径，使用的肯定是绝对路径，标准的绝对路径的写法
                /项目名/具体资源路径

                转发：
                    使用的路径是一种特殊的绝对路径，这种路径前面不加/项目名，这种路径在项目中也被称为项目的内部路径
                    /login.jsp

                重定向：
                    使用的路径就是传统的绝对路径，前面必须加/项目名
                    /crm/login.jsp

                request.getContextPath():
                    动态得到当前项目的  /项目名
             */

                response.sendRedirect(request.getContextPath() + "/login.jsp");



            }

        }






    }
}

































