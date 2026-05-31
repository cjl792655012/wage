package com.hhgz.wage.dto;

import lombok.Data;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/5/30 22:45
 */
@Data
public class CustomerDebtDTO {

    /**
     * id
     */
    private String addId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 欠款金额
     */
    private String debt;

    /**
     * 欠款开始日期
     */
    private String debtStartDate;

}