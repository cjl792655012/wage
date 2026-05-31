package com.hhgz.wage.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 客户欠款列表响应实体
 * @author: JinLong Cai
 * @date: 2026/5/29 23:04
 */
@Data
public class CustomerDebtResp {

    /**
     * 错误码 0成功
     */
    @JsonProperty("err")
    private Integer err;

    /**
     * 客户列表
     */
    @JsonProperty("list")
    private List<CustomerItem> list;

    /**
     * 列表总数
     */
    @JsonProperty("list_num")
    private String listNum;

    /**
     * 是否最后一页 0否 1是
     */
    @JsonProperty("is_last")
    private Integer isLast;

    /**
     * 提示信息
     */
    @JsonProperty("msg")
    private String msg;

}