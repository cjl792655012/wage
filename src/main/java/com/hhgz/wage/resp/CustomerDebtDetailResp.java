package com.hhgz.wage.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/5/30 22:33
 */
@Data
public class CustomerDebtDetailResp {

    /**
     * 错误码 0成功
     */
    @JsonProperty("err")
    private Integer err;

    /**
     * 客户欠款详情列表
     */
    @JsonProperty("list")
    private List<CustomerDetail> list;

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