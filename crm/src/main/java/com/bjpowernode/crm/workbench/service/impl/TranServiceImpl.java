package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：杜聚宾
 */
public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    @Override
    public boolean save(Tran t, String customerName) {

        /*

            优先处理客户，通过客户名称查询有没有这个客户，如果有将客户的id取出
                             如果没有该客户，则新建一个客户，将客户的id取出


            优先处理客户的目的就是为了取得id，封装到t对象中

         */

        boolean flag = true;

        Customer cus = customerDao.getCustomerByName(customerName);
        //如果cus为null说明没有该客户
        if(cus==null){

            //需要新建客户
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setNextContactTime(t.getNextContactTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setOwner(t.getOwner());

            int count1 = customerDao.save(cus);
            if(count1!=1){
                flag = false;
            }

        }

        String customerId = cus.getId();
        t.setCustomerId(customerId);

        //创建交易
        int count2 = tranDao.save(t);
        if(count2!=1){
            flag = false;
        }

        //创建交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());

        int count3 = tranHistoryDao.save(th);
        if(count3!=1){
            flag = false;
        }


        return flag;
    }

    @Override
    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(tranId);

        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {

        boolean flag = true;

        //改变阶段
        int count1 = tranDao.changeStage(t);
        if(count1!=1){

            flag = false;

        }


        //改变阶段之后，需要新增一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setStage(t.getStage());
        th.setTranId(t.getId());

        int count2 = tranHistoryDao.save(th);

        if(count2!=1){

            flag = false;

        }


        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {

        //取total
        int total = tranDao.getTotal();

        //取dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();

        //将total和dataList保存到map中
        Map<String,Object> map = new HashMap<>();
        map.put("total", total);
        map.put("dataList", dataList);

        //返回map
        return map;
    }
}































