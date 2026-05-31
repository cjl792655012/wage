package com.hhgz.wage.service;

import com.hhgz.wage.dto.CustomerDebtDTO;
import com.hhgz.wage.req.CustomerDebtDetailReq;
import com.hhgz.wage.req.CustomerQueryReq;
import com.hhgz.wage.resp.CustomerDebtDetailResp;
import com.hhgz.wage.resp.CustomerDebtResp;
import com.hhgz.wage.resp.CustomerItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private static final String BASE_QUERY_URL = "https://api.duoke.net/index.php/customer/get_by_order";

    private static final String BASE_ORDER_URL = "https://api.duoke.net/index.php/order/search_by_id";


    public void createCustomerDebtExcel() throws IOException {

        List<CustomerDebtDTO> customerDebtList = new ArrayList<>();

        Integer isLast = 0;
        Integer num = 0;
        outer:
        while (isLast == 0) {

            num = num + 1;

            // 1. 构建请求参数（完全按照你的URL）
            CustomerQueryReq request = createCustomerQueryRequest(num, 50);

            // 2. 拼接 GET 请求URL（自动编码）
            String url = UriComponentsBuilder.fromHttpUrl(BASE_QUERY_URL)
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
                    .encode()
                    .toUriString();

            // 3. 发送请求并解析为你的Response实体
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CustomerDebtResp> response = restTemplate.getForEntity(
                    url,
                    CustomerDebtResp.class
            );

            // 4. 返回结果
            CustomerDebtResp customerDebtResp = response.getBody();
            isLast = customerDebtResp.getIsLast();
            List<CustomerItem> customerItemList = customerDebtResp.getList();
            for (CustomerItem customerItem : customerItemList) {
                if (Double.valueOf(customerItem.getDebt()) <= 0.0) {
                    break outer;
                }
                if ("总欠款：".equals(customerItem.getName())) {
                    continue;
                }
                //查询顾客详情列表

                //第一次
                CustomerDebtDetailReq customerDebtDetailReq = createCustomerDebtDetailReq(customerItem.getName(), 1, 20);
                CustomerDebtDetailResp customerDebtDetailResp = sendCustomerDebtDetailReq(customerDebtDetailReq);
                //第二次
                CustomerDebtDetailReq customerDebtDetailReq2 = createCustomerDebtDetailReq(customerItem.getName(),
                        Integer.valueOf(customerDebtDetailResp.getListNum()), Integer.valueOf(customerDebtDetailResp.getListNum()));
                CustomerDebtDetailResp customerDebtDetailResp2 = sendCustomerDebtDetailReq(customerDebtDetailReq2);
                //封装
                CustomerDebtDTO debtDTO = new CustomerDebtDTO();
                debtDTO.setAddId(customerItem.getAddId());
                debtDTO.setCustomerName(customerItem.getName());
                debtDTO.setDebt(customerItem.getDebt());
                String createTimeStr = customerDebtDetailResp2.getList().get(0).getCreateTime();
                Long createTime = Long.valueOf(createTimeStr);
                Date createDate = new Date(createTime * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formatTime = sdf.format(createDate);
                debtDTO.setDebtStartDate(formatTime);
                customerDebtList.add(debtDTO);
            }
        }

        Workbook workbook = new XSSFWorkbook(); // 使用XSSFWorkbook创建一个.xlsx格式的工作簿
        Sheet sheet = workbook.createSheet();
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("姓名");
        headerRow.createCell(1).setCellValue("欠款");
        headerRow.createCell(2).setCellValue("欠款开始日期");

        for (int i = 0; i < customerDebtList.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(customerDebtList.get(i).getCustomerName());
            dataRow.createCell(1).setCellValue(customerDebtList.get(i).getDebt());
            dataRow.createCell(2).setCellValue(customerDebtList.get(i).getDebtStartDate());
        }

        // 将工作簿写入文件或输出流
        FileOutputStream fileOut = new FileOutputStream("wage.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }

    /**
     * 创建客户欠款列表查询请求体
     *
     * @param page    页码
     * @param pageNum 每页行数
     * @return 客户欠款列表查询请求体
     */
    private CustomerQueryReq createCustomerQueryRequest(Integer page, Integer pageNum) {
        CustomerQueryReq request = new CustomerQueryReq();
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

    private CustomerDebtDetailReq createCustomerDebtDetailReq(String customerName, Integer page, Integer pageNum) {
        CustomerDebtDetailReq request = new CustomerDebtDetailReq();
        request.setBi_key("documentList");
        request.setKeyword(customerName);
        request.setPage(String.valueOf(page));
        request.setPage_num(String.valueOf(pageNum));
        request.setType("custom");
        request.setSday("2020-01-01");
        request.setEday("2099-12-31");
        request.setOrder_type("{\"options\":[\"sale_order\",\"retail_order\"],\"allSelected\":0}");
        request.setPay_status("{\"options\":[0,1],\"allSelected\":0}");
        request.setLang("chs");
        request.setKey("5_94c5a945f5acb10686cda49f5daf23e7");

        return request;
    }

    private CustomerDebtDetailResp sendCustomerDebtDetailReq(CustomerDebtDetailReq customerDebtDetailReq) throws UnsupportedEncodingException {
        //中文编码
        String keyword = URLEncoder.encode(customerDebtDetailReq.getKeyword(), "UTF-8");
        String orderType = URLEncoder.encode(customerDebtDetailReq.getOrder_type(), "UTF-8");
        String payStatus = URLEncoder.encode(customerDebtDetailReq.getPay_status(), "UTF-8");

        // 2. 自动拼接GET请求（自动编码，不会乱码）
        String url = UriComponentsBuilder.fromHttpUrl(BASE_ORDER_URL)
                .queryParam("bi_key", customerDebtDetailReq.getBi_key())
                .queryParam("keyword", keyword)
                .queryParam("page", customerDebtDetailReq.getPage())
                .queryParam("page_num", customerDebtDetailReq.getPage_num())
                .queryParam("type", customerDebtDetailReq.getType())
                .queryParam("sday", customerDebtDetailReq.getSday())
                .queryParam("eday", customerDebtDetailReq.getEday())
                .queryParam("order_type", orderType)
                .queryParam("pay_status", payStatus)
                .queryParam("lang", customerDebtDetailReq.getLang())
                .queryParam("key", customerDebtDetailReq.getKey())
                .build()
                .toUriString();

        // 3. 发送请求，自动解析为你的Response实体
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CustomerDebtDetailResp> response = restTemplate.getForEntity(
                url,
                CustomerDebtDetailResp.class
        );
        return response.getBody();
    }

}