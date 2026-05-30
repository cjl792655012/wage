package com.hhgz.wage.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 客户列表项
 * @author: JinLong Cai
 * @date: 2026/5/29 23:05
 */

@Data
public class CustomerItem {

    @JsonProperty("type")
    private String type;

    @JsonProperty("client_shop_id")
    private List<String> clientShopId;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("add_id")
    private String addId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("vip")
    private String vip;

    @JsonProperty("is_premium")
    private String isPremium;

    @JsonProperty("mask_auth")
    private String maskAuth;

    @JsonProperty("debt")
    private String debt;

    @JsonProperty("days_not_purchased")
    private Integer daysNotPurchased;

    @JsonProperty("days_not_payed")
    private Integer daysNotPayed;

    @JsonProperty("cat1_id")
    private String cat1Id;

    @JsonProperty("staff_id")
    private String staffId;

    @JsonProperty("discount")
    private String discount;

    @JsonProperty("cat2_id")
    private String cat2Id;

    @JsonProperty("cat3_id")
    private String cat3Id;

    @JsonProperty("cat4_id")
    private String cat4Id;

    @JsonProperty("cat5_id")
    private String cat5Id;

    @JsonProperty("point")
    private String point;

    @JsonProperty("current_point")
    private String currentPoint;

    @JsonProperty("total_point")
    private String totalPoint;

    @JsonProperty("id")
    private String id;

    @JsonProperty("staff_name")
    private String staffName;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("birthday")
    private String birthday;

    @JsonProperty("birthday_days")
    private String birthdayDays;

    @JsonProperty("age")
    private String age;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("ctime")
    private String ctime;

    @JsonProperty("wxuser_id")
    private String wxuserId;

    @JsonProperty("disable")
    private String disable;

    @JsonProperty("currency_debt")
    private String currencyDebt;

    @JsonProperty("currency_symbol")
    private String currencySymbol;

    @JsonProperty("goods_num")
    private Integer goodsNum;
}