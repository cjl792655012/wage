package com.hhgz.wage.service;

import com.hhgz.wage.req.CustomerQueryRequest;
import com.hhgz.wage.resp.CustomerDebtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2026/5/29 22:59
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    // 接口基础地址
    private static final String BASE_URL = "https://api.duoke.net/index.php/customer/get_by_order";


    public String createCustomerDebtExcel() {
        // 1. 构建请求参数（完全按照你的URL）
        CustomerQueryRequest request = createCustomerQueryRequest(1, 5);

        // 2. 拼接 GET 请求URL（自动编码）
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("bi_key", request.getBi_key())
                .queryParam("days", request.getDays())
                .queryParam("order", request.getOrder())
                .queryParam("isasc", request.getIsasc())
                .queryParam("keyword", request.getKeyword())
                .queryParam("page", request.getPage())
                .queryParam("page_num", request.getPage_num())
                .queryParam("client_status", request.getClient_status())
                .queryParam("type", request.getType())
                .queryParam("lang", request.getLang())
                .queryParam("key", request.getKey())
                .build()
                .toUriString();

        // 3. 发送请求并解析为你的Response实体
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CustomerDebtResponse> response = restTemplate.getForEntity(
                url,
                CustomerDebtResponse.class
        );

        // 4. 返回结果
        return response.getBody().toString();
    }

    /**
     * 创建客户欠款列表查询请求体
     *
     * @param page    页码
     * @param pageNum 每页行数
     * @return 客户欠款列表查询请求体
     */
    private CustomerQueryRequest createCustomerQueryRequest(Integer page, Integer pageNum) {
        CustomerQueryRequest request = new CustomerQueryRequest();
        request.setBi_key("clientList");
        request.setDays("-1");
        request.setOrder("debt");
        request.setIsasc("0");
        request.setKeyword("");
        request.setPage(String.valueOf(page));
        request.setPage_num(String.valueOf(pageNum));
        request.setType("1");
        request.setLang("chs");
        request.setKey("5_94c5a945f5acb10686cda49f5daf23e7");
        return request;
    }

}