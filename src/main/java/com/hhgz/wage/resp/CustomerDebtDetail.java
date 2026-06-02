package com.hhgz.wage.resp;

import lombok.Data;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/6/2 21:44
 */
@Data
public class CustomerDebtDetail {

    private String id;
    private String number;
    private String type;
    private String is_before;
    private String ctime;
    private String total_price;

}