package com.bjpowernode.crm.settings.dao;

import com.bjpowernode.crm.settings.domain.DicValue;

import java.util.List;

/**
 * 作者：北京动力节点老崔
 */
public interface DicValueDao {
    List<DicValue> getValueListByCode(String code);
}
