package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：杜聚宾
 */
public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户模块控制器");

        //url-pattern
        String path = request.getServletPath();

        if("/settings/user/login.do".equals(path)){

            login(request,response);

        }else if("/settings/user/xxx.do".equals(path)){

            //xxx(request,response);

        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行验证登录操作");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");

        //将密码明文解析为密文
        loginPwd = MD5Util.getMD5(loginPwd);

        //取得浏览器端ip地址
        /*

            如果我们访问的是自己的机器：
            有可能我们敲的不是ip，而是localhost，那么我们通过以下方法得到的ip地址：00000001
            我们可以使用127.0.0.1来代替localhost，这是一个有效的代表本机的ip地址


         */
        String ip = request.getRemoteAddr();

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{

            User user = us.login(loginAct,loginPwd,ip);

            request.getSession().setAttribute("user", user);

            //登录成功后我们需要为前端返回
            //{"success":true}
            /*String str = "{\"success\":true}";
            response.getWriter().print(str);*/

            PrintJson.printJsonFlag(response, true);


        }catch(Exception e){

            e.printStackTrace();

            //登录失败后我们需要为前端返回
            //{"success":false,"msg":?}

            //取得异常信息
            String msg = e.getMessage();

            Map<String,Object> map = new HashMap<>();
            map.put("success", false);
            map.put("msg", msg);

            //将map解析为json串{"success":false,"msg":?}
            PrintJson.printJsonObj(response, map);


        }




    }
}
























