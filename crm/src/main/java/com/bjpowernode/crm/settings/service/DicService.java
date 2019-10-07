package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

/**
 * 作者：北京动力节点老崔
 */
public interface DicService {
    Map<String, List<DicValue>> getAll();
}
