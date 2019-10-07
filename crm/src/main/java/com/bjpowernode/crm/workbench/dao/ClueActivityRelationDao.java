package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    int delete(String id);

    int save(ClueActivityRelation car);

    List<ClueActivityRelation> getListByClueId(String clueId);
}
