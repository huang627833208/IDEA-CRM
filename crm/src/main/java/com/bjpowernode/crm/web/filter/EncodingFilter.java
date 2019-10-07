package com.bjpowernode.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 作者：杜聚宾
 */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("进入到过滤字符编码的过滤器");

        //处理post请求中文参数的问题
        req.setCharacterEncoding("UTF-8");

        //处理响应流响应中文信息的问题
        resp.setContentType("text/html;charset=utf-8");

        //使用过滤器链将请求放行
        chain.doFilter(req, resp);

    }
}
