package com.hhgz.wage.mysql.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 考勤时间表
 *
 * @author jl.cai
 * @TableName attendance_time
 */
@Data
@TableName(value = "attendance_time")
public class AttendanceTime implements Serializable {

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    @TableField(value = "id")
    private Integer id;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @TableField(value = "name")
    private String name;

    /**
     * 请假天数
     */
    @ApiModelProperty(value = "请假天数")
    @TableField(value = "day")
    private Integer day;

    /**
     * 请假小时数
     */
    @ApiModelProperty(value = "请假小时数")
    @TableField(value = "hour")
    private Integer hour;

    /**
     * 请假分钟数
     */
    @ApiModelProperty(value = "请假分钟数")
    @TableField(value = "minute")
    private Integer minute;

    /**
     * 午餐扣减系数
     */
    @ApiModelProperty(value = "午餐扣减系数")
    @TableField(value = "lunch_dec")
    private Integer lunchDec;

    /**
     * 插入时间
     */
    @ApiModelProperty(value = "插入时间")
    @TableField(value = "insert_time")
    private Date insertTime;

}
