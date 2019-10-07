package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：杜聚宾
 */
public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到交易控制器");

        //url-pattern
        String path = request.getServletPath();

        if("/workbench/transaction/add.do".equals(path)){

            add(request,response);

        }else if("/workbench/transaction/getCustomerNameList.do".equals(path)){

            getCustomerNameList(request,response);

        }else if("/workbench/transaction/save.do".equals(path)){

            save(request,response);

        }else if("/workbench/transaction/detail.do".equals(path)){

            detail(request,response);

        }else if("/workbench/transaction/getHistoryListByTranId.do".equals(path)){

            getHistoryListByTranId(request,response);

        }else if("/workbench/transaction/changeStage.do".equals(path)){

            changeStage(request,response);

        }else if("/workbench/transaction/getCharts.do".equals(path)){

            getCharts(request,response);

        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得交易阶段相关统计图表的数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*

            total
            dataList

         */
        Map<String,Object> map = ts.getCharts();

        PrintJson.printJsonObj(response, map);


    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到改变阶段的操作");

        String id = request.getParameter("id");
        String money = request.getParameter("money");
        String stage = request.getParameter("stage");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran t = new Tran();

        t.setId(id);
        t.setStage(stage);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.changeStage(t);

        //取得阶段和可能性之间的对应关系
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);

        Map<String,Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("t", t);

        PrintJson.printJsonObj(response, map);


    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据交易id取得相应的历史信息列表");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> thList = ts.getHistoryListByTranId(tranId);

        //取得阶段和可能性之间的对应关系
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");

        //遍历每一条历史，处理可能性
        for(TranHistory th : thList){

            th.setPossibility(pMap.get(th.getStage()));

        }

        PrintJson.printJsonObj(response, thList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("跳转到交易详细信息页");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);

        //取出该记录的阶段
        String stage = t.getStage();

        //取得阶段和可能性之间的对应关系
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");

        //根据pMap和stage取得可能性
        String possibility =  pMap.get(stage);

        t.setPossibility(possibility);

        request.setAttribute("t", t);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("执行添加交易的操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName"); //注意：我们现在只有客户名称（id还没有）
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setType(type);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setActivityId(activityId);
        t.setCreateBy(createBy);
        t.setCreateTime(createTime);
        t.setSource(source);
        t.setOwner(owner);
        t.setNextContactTime(nextContactTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setContactsId(contactsId);

        //此时t缺失了一个customerId

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t,customerName);

        if(flag){

            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

        }


    }

    private void getCustomerNameList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得客户名称列表（根据客户名称关键字进行模糊查询）");

        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> sList = cs.getCustomerNameList(name);

        PrintJson.printJsonObj(response, sList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("跳转到交易添加页");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        request.setAttribute("uList", uList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);

    }

}
























