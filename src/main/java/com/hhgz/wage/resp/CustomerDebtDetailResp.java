package com.hhgz.wage.resp;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/6/2 21:42
 */
@Data
public class CustomerDebtDetailResp {

    private Integer err;
    private String msg;
    private List<CustomerDebtDetail> ret;

}