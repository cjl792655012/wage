package com.hhgz.wage.mysql.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jl.cai
 * @TableName person
 */
@Data
@TableName(value = "person")
public class Person implements Serializable {

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
     * 基础工资
     */
    @ApiModelProperty(value = "基础工资")
    @TableField(value = "base_wage")
    private Integer baseWage;

}
