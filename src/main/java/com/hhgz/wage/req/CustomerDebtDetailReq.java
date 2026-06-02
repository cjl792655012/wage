package com.hhgz.wage.req;

import lombok.Data;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/6/2 21:34
 */
@Data
public class CustomerDebtDetailReq {

    /**
     * app_pid
     */
    private String app_pid = "32";
    /**
     * 排序类型（JSON字符串）
     */
    private String order_type = "all_order";
    /**
     * 语言：chs
     */
    private String lang = "chs";
    /**
     * 接口密钥
     */
    private String key = "2_2013c9ebe165665728b4824907cc3f31";
    /**
     * 客户id
     */
    private String client_id;

}