package com.hhgz.wage.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/5/30 22:37
 */
@Data
public class CustomerDetail {

    @JsonProperty("id")
    private String id;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("create_time")
    private String createTime;
}