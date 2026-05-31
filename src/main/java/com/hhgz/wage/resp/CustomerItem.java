package com.hhgz.wage.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: 客户列表项
 * @author: JinLong Cai
 * @date: 2026/5/29 23:05
 */

@Data
public class CustomerItem {


    @JsonProperty("add_id")
    private String addId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("debt")
    private String debt;
}