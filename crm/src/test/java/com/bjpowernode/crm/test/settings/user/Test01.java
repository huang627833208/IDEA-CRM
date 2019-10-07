package com.bjpowernode.crm.test.settings.user;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：杜聚宾
 */
public class Test01 {

    public static void main(String[] args) {

        /*

            expireTime:失效时间
            lockState:锁定状态
            allowIps:ip地址群

         */

        //失效时间
        /*String expireTime = "2018-06-10 10:10:10";
        //当前系统时间
        *//*Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        System.out.println(currentTime);*//*
        //通过util包下的工具取得当前系统时间
        String currentTime = DateTimeUtil.getSysTime();

        int count = expireTime.compareTo(currentTime);

        if(count<0){
            System.out.println("账号已失效");
        }*/

        //锁定状态
        /*String lockState = "0";
        if("0".equals(lockState)){

            System.out.println("账号已锁定");

        }*/

        //服务器取得浏览器的ip地址
        //String ip = request.getRemoteAddr();
        /*String ip = "192.168.1.6";

        //可访问的ip地址群
        String allowIps = "192.168.1.1,192.168.1.2,192.168.1.3";

        if(!allowIps.contains(ip)){

            System.out.println("ip地址受限");

        }*/

        //MD5

        //明文
        String pwd = "Bjpowernode123@163.com";

        //密文
        pwd = MD5Util.getMD5(pwd);

        System.out.println(pwd);



    }

}








































