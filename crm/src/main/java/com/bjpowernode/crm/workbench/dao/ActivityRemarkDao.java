package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

/**
 * 作者：北京动力节点老崔
 */
public interface ActivityRemarkDao {
    int getCountByAid(String[] ids);

    int deleteByAid(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String aid);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);
}
