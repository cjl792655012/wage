package com.hhgz.wage.req;

import lombok.Data;

/**
 * @description: 客户列表查询请求参数
 * @author: JinLong Cai
 * @date: 2026/5/29 23:17
 */
@Data
public class CustomerQueryRequest {

    /**
     * 固定值：clientList
     */
    private String bi_key;

    /**
     * 天数：-1=全部
     */
    private String days;

    /**
     * 排序字段：debt
     */
    private String order;

    /**
     * 排序方式：0=降序
     */
    private String isasc;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 当前页码
     */
    private String page;

    /**
     * 每页条数
     */
    private String page_num;

    /**
     * 客户端状态（JSON字符串格式）
     */
    private String client_status;

    /**
     * 类型：1
     */
    private String type;

    /**
     * 语言：chs
     */
    private String lang;

    /**
     * 接口密钥
     */
    private String key;
}