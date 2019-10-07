package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：杜聚宾
 */
public class UserServiceImpl implements UserService {

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        Map<String,String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        User user = userDao.login(map);

        if(user==null){

            //说明账号密码错误，没有必要继续向下验证了
            //以抛出自定义异常的方式，来处理错误消息的问题
            throw new LoginException("账号密码错误");

        }

        //如果程序能够走到该行，说明账号密码是正确的
        //需要从user对象中取出其他验证项，继续进行验证
        String expireTime = user.getExpireTime();
        if(expireTime.compareTo(DateTimeUtil.getSysTime())<0){

            throw new LoginException("账号已失效");

        }

        String lockState = user.getLockState();
        if("0".equals(lockState)){

            throw new LoginException("账号已锁定");

        }

        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){

            throw new LoginException("ip地址受限");

        }

        //如果程序能够成功的走到该行，说明没有抛出任何异常，登录成功，返回user对象
        return user;
    }

    @Override
    public List<User> getUserList() {

        List<User> uList = userDao.getUserList();

        return uList;
    }
}






























