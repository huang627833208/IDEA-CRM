package com.bjpowernode.crm.settings.domain;

/**
 * 作者：杜聚宾
 */
public class DicValue {

    private String id;
    private String value;
    private String text;
    private String orderNo;
    private String typeCode;

    public String getId() {
        return id;
    }

    public DicValue setId(String id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public DicValue setValue(String value) {
        this.value = value;
        return this;
    }

    public String getText() {
        return text;
    }

    public DicValue setText(String text) {
        this.text = text;
        return this;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public DicValue setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public DicValue setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }
}
