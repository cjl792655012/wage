package com.hhgz.wage.req;

import lombok.Data;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/5/30 23:10
 */
@Data
public class CustomerDebtOrderReq {

    private String bi_key;         // 固定：documentList
    private String keyword;       // 搜索关键词
    private String page;          // 页码
    private String page_num;      // 每页条数
    private String type;          // 固定：custom
    private String sday;          // 开始日期
    private String eday;          // 结束日期
    private String order_type;    // 订单类型（JSON字符串）
    private String pay_status;    // 支付状态（JSON字符串）
    private String lang;          // 语言：chs
    private String key;           // 接口密钥

}