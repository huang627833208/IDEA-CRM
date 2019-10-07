package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 作者：杜聚宾
 */
public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue c) {

        boolean flag = true;

        int count = clueDao.save(c);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    @Override
    public Clue detail(String id) {

        Clue c = clueDao.detail(id);

        return c;
    }

    @Override
    public boolean unbund(String id) {

        boolean flag = true;

        int count = clueActivityRelationDao.delete(id);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    @Override
    public boolean bund(String clueId, String[] activityIds) {

        boolean flag = true;

        //遍历市场活动id
        for(String activityId : activityIds){

            //根据每一个activityId和clueId做关联
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setActivityId(activityId);
            car.setClueId(clueId);

            //执行关联关系表的添加操作
            int count = clueActivityRelationDao.save(car);
            if(count!=1){

                flag = false;

            }

        }

        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {

        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;

        //开始业务转换流程
        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        //将来做创建客户和创建联系人的时候，里面的属性信息都是来自于线索对象c
        Clue c = clueDao.getById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配（使用=），判断该客户是否存在！）
        //取得公司名称
        String company = c.getCompany();

        Customer cus = customerDao.getCustomerByName(company);

        //如果cus为null，说明没有这个客户
        if(cus==null){

            //新建一个客户
            cus = new Customer();

            cus.setId(UUIDUtil.getUUID());
            cus.setName(company);
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());
            cus.setAddress(c.getAddress());

            int count1 = customerDao.save(cus);
            if(count1!=1){
                flag = false;
            }


        }

        //==========================================================================
        /*

            以上我们是处理完了客户
            将来的流程，如果使用到客户的id，我们就可以通过cus.getId()

         */

        //(3)通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());

        int count2 = contactsDao.save(con);
        if(count2!=1){
            flag = false;
        }

        //==========================================================================
        /*

            以上我们是处理完了联系人
            将来的流程，如果使用到联系人的id，我们就可以通过con.getId()

         */

        //(4)线索备注转换到客户备注以及联系人备注
        //查询得到线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getRemarkListByClueId(clueId);
        //遍历线索备注列表
        for(ClueRemark clueRemark : clueRemarkList){

            //取得每一个备注信息
            //取得的备注信息同时也是线索备注表唯一需要备份（转换）的信息
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setEditFlag("0");
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3!=1){
                flag = false;
            }

            //创建联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setEditFlag("0");
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4!=1){
                flag = false;
            }


        }


        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //查询出与该线索和市场活动的关联关系信息列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        //遍历关联关系列表
        for(ClueActivityRelation clueActivityRelation :clueActivityRelationList){

            //取出每一个市场活动
            String activityId = clueActivityRelation.getActivityId();

            //让每一个市场活动与联系人重新做关联
            //从第三步生成的联系人取出其id值
            String contactsId = con.getId();

            //添加联系人和市场活动的关联关系
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contactsId);
            contactsActivityRelation.setActivityId(activityId);

            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1){
                flag = false;
            }

        }

        //(6) 如果有创建交易需求，创建一条交易
        //如果t不为null，则证明需要创建交易
        if(t!=null){

            /*

                在控制器中已经为t对象封装了一些信息
                id createTime createBy money name expecteDate stage activityId

                其他的信息我们如果能够从线索对象c中取得，就尽量为t对象多封装一些信息

             */

            //补充信息
            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());

            //添加交易
            int count6 = tranDao.save(t);
            if(count6!=1){
                flag = false;
            }


            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            //添加交易历史
            int count7 = tranHistoryDao.save(th);
            if(count7!=1){
                flag = false;
            }


        }

        //(8) 删除线索备注
        for(ClueRemark clueRemark : clueRemarkList){

            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8!=1){
                flag = false;
            }

        }

        //(9) 删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation :clueActivityRelationList){

            int count9 = clueActivityRelationDao.delete(clueActivityRelation.getId());
            if(count9!=1){
                flag = false;
            }

        }

        //(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if(count10!=1){
            flag = false;
        }


        return flag;
    }


}


















































