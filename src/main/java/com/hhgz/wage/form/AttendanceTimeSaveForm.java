package com.hhgz.wage.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/7/6 14:25
 */
@Data
@ToString(callSuper = true)
public class AttendanceTimeSaveForm implements Serializable {

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @NotEmpty
    private String name;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    @NotEmpty
    private String month;

    /**
     * 请假天数
     */
    @ApiModelProperty(value = "请假天数")
    private Integer day;

    /**
     * 请假小时数
     */
    @ApiModelProperty(value = "请假小时数")
    private Integer hour;

    /**
     * 请假分钟数
     */
    @ApiModelProperty(value = "请假分钟数")
    private Integer minute;

}