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
public class CustomerDebtResponse {

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
     * 采购数量
     */
    @JsonProperty("purchase_num")
    private String purchaseNum;

    /**
     * 高级会员数量
     */
    @JsonProperty("premium_num")
    private String premiumNum;

    /**
     * 认证确认数量
     */
    @JsonProperty("auth_confirm_num")
    private Integer authConfirmNum;

    /**
     * 提示信息
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 调试信息
     */
    @JsonProperty("debug_msg")
    private String debugMsg;
}