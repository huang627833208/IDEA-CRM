package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.User;

import java.util.List;

/**
 * 作者：北京动力节点老崔
 */
public interface UserService {


    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
