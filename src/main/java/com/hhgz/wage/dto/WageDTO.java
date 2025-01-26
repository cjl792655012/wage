package com.hhgz.wage.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/7/6 15:22
 */
@Data
@ToString(callSuper = true)
public class WageDTO {

    private String name;

    private Integer baseWage;

    private Integer calDay;

    private Integer calHour;

    private Integer calMinute;

    private Integer effWorkDay;

    private Integer calWage;

    private Integer calLunchFee;

    private Integer calLunchFeeByX;

    private Integer calLunchFeeByA;

    private Integer calHighTempFee;

    /**
     * 全勤奖
     */
    private Integer fullAward;

    private Integer totalWage;

    private Integer totalWageByX;

    private Integer totalWageByA;

}