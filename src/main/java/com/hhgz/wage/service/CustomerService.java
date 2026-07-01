package com.hhgz.wage.service;

import cn.hutool.core.collection.CollectionUtil;
import com.hhgz.wage.dto.CustomerDebtDTO;
import com.hhgz.wage.req.CustomerDebtDetailReq;
import com.hhgz.wage.req.CustomerDebtOrderReq;
import com.hhgz.wage.req.CustomerQueryReq;
import com.hhgz.wage.resp.*;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final String BASE_ORDER_DETAIL_URL = "https://api.duoke.net/index.php/order/get_unpaiddoc_by_client_id";

    // 日期格式：根据你实际格式修改（yyyy-MM-dd / yyyy/MM/dd 等）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 空日期的key
    private static final String EMPTY_DATE_KEY = "emptyDate";


    public void createCustomerDebtExcel() throws IOException {

        List<CustomerDebtDTO> customerDebtList = new ArrayList<>();

        Integer isLast = 0;
        Integer cycleNum = 0;
        Integer customerNum = 0;
        outer:
        while (isLast == 0) {

            cycleNum = cycleNum + 1;

            // 1. 构建请求参数（完全按照你的URL）
            CustomerQueryReq request = createCustomerQueryRequest(cycleNum, 50);

            // 2. 拼接 GET 请求URL（统一编码一次，传 URI 避免 RestTemplate 二次编码）
            URI uri = UriComponentsBuilder.fromHttpUrl(BASE_QUERY_URL).queryParam("bi_key", request.getBi_key()).queryParam("days", request.getDays()).queryParam("order", request.getOrder()).queryParam("isasc", request.getIsasc()).queryParam("keyword", request.getKeyword()).queryParam("page", request.getPage()).queryParam("page_num", request.getPage_num()).queryParam("client_status", request.getClient_status()).queryParam("type", request.getType()).queryParam("lang", request.getLang()).queryParam("key", request.getKey()).build().encode().toUri();

            // 3. 发送请求并解析为你的Response实体
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CustomerDebtResp> response = restTemplate.getForEntity(uri, CustomerDebtResp.class);

            // 4. 返回结果
            CustomerDebtResp customerDebtResp = response.getBody();
            isLast = customerDebtResp.getIsLast();
            List<CustomerItem> customerItemList = customerDebtResp.getList();
            for (CustomerItem customerItem : customerItemList) {
                if (Double.valueOf(customerItem.getDebt()) <= 30.0) {
                    break outer;
                }
                if ("总欠款：".equals(customerItem.getName())) {
                    continue;
                }
                customerNum = customerNum + 1;
                System.out.println("========== 序号：" + customerNum + " | id：" + customerItem.getAddId() + " | 客户：" + customerItem.getName() + " ==========");
                //查询顾客详情列表
                CustomerDebtDetailReq customerDebtDetailReq = createCustomerDebtDetailReq(customerItem.getAddId());
                CustomerDebtDetailResp customerDebtDetailResp = sendCustomerDebtDetailReq(customerDebtDetailReq);
                List<CustomerDebtDetail> customerDebtDetails = customerDebtDetailResp.getRet();
                String formatTime;
                Double firstDebtNum = 0.0;
                if (CollectionUtil.isNotEmpty(customerDebtDetails)) {
                    CustomerDebtDetail customerDebtDetail = customerDebtDetails.get(customerDebtDetails.size() - 1);
                    String createTimeStr = customerDebtDetail.getCtime();
                    Long createTime = Long.valueOf(createTimeStr);
                    Date createDate = new Date(createTime * 1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    formatTime = sdf.format(createDate);
                    firstDebtNum = Double.parseDouble(customerDebtDetail.getTotal_price()) - Double.parseDouble(customerDebtDetail.getPaid_price());
                } else {
                    formatTime = "";
                }
                //封装
                CustomerDebtDTO debtDTO = new CustomerDebtDTO();
                debtDTO.setAddId(customerItem.getAddId());
                debtDTO.setCustomerName(customerItem.getName());
                debtDTO.setDebt(customerItem.getDebt());
                debtDTO.setDebtStartDate(formatTime);
                debtDTO.setFirstDebtNum(firstDebtNum);
                customerDebtList.add(debtDTO);
            }
        }

        //使用XSSFWorkbook创建一个.xlsx格式的工作簿
        Workbook workbook = new XSSFWorkbook();
        //获取退货抵扣列表
        getRefundDeductionList(customerDebtList);
        //排序
        Map<String, List<CustomerDebtDTO>> result = groupAndSortAll(customerDebtList);
        for (Map.Entry<String, List<CustomerDebtDTO>> entry : result.entrySet()) {
            Sheet sheet = workbook.createSheet(entry.getKey());
            //设置为204%缩放
            sheet.setZoom(190);
            //设置列宽度为12个字符
            sheet.setColumnWidth(0, 33 * 256);
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("姓名");
            headerRow.createCell(1).setCellValue("欠款");
            headerRow.createCell(2).setCellValue("欠款开始日期");

            for (int i = 0; i < entry.getValue().size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(entry.getValue().get(i).getCustomerName());
                dataRow.createCell(1).setCellValue(entry.getValue().get(i).getDebt());
                dataRow.createCell(2).setCellValue(entry.getValue().get(i).getDebtStartDate());
            }
        }


        // 将工作簿写入文件或输出流
        FileOutputStream fileOut = new FileOutputStream("debt.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }

    private void getRefundDeductionList(List<CustomerDebtDTO> customerDebtList) {
        System.out.println("========================= getRefundDeductionList =========================");
        for (CustomerDebtDTO dto : customerDebtList) {
            if (dto.getFirstDebtNum() < 0.0) {
                System.out.println("id: " + dto.getAddId() + ", 姓名: " + dto.getCustomerName() + ", 日期: " + dto.getDebtStartDate());
            }
        }
        System.out.println("======================= getRefundDeductionList ===========================");
    }

    /**
     * 按年份分组 + 年份排序 + 每个年份内按月份升序排序
     *
     * @param debtList 原始集合
     * @return 有序Map：年份从小到大，每个年份数据按月排序
     */
    public static Map<String, List<CustomerDebtDTO>> groupAndSortAll(List<CustomerDebtDTO> debtList) {
        Map<String, List<CustomerDebtDTO>> resultMap = new LinkedHashMap<>();

        if (debtList == null || debtList.isEmpty()) {
            return resultMap;
        }

        // ===================== 1. 拆分：空日期 和 有效日期 =====================
        List<CustomerDebtDTO> emptyDateList = new ArrayList<>();
        List<CustomerDebtDTO> validDateList = new ArrayList<>();

        for (CustomerDebtDTO dto : debtList) {
            String dateStr = dto.getDebtStartDate();
            if (dateStr == null || dateStr.trim().isEmpty()) {
                emptyDateList.add(dto);
            } else {
                validDateList.add(dto);
            }
        }

        // ===================== 2. 有效日期：按年份分组 + 排序 =====================
        if (!validDateList.isEmpty()) {
            // 按年份分组（使用Integer保证排序正确）
            Map<Integer, List<CustomerDebtDTO>> yearGroupMap = validDateList.stream().collect(Collectors.groupingBy(dto -> {
                LocalDate date = LocalDate.parse(dto.getDebtStartDate(), DATE_FORMATTER);
                return date.getYear();
            }));

            // 每个年份内部 → 按月份升序
            yearGroupMap.values().forEach(list -> list.sort(Comparator.comparing(dto -> {
                LocalDate date = LocalDate.parse(dto.getDebtStartDate(), DATE_FORMATTER);
                return date.getMonthValue();
            })));

            // 年份升序排列，并放入结果
            yearGroupMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> resultMap.put(String.valueOf(entry.getKey()), entry.getValue()));
        }

        // 空日期放入结果
        if (!emptyDateList.isEmpty()) {
            resultMap.put(EMPTY_DATE_KEY, emptyDateList);
        }

        return resultMap;
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

    private CustomerDebtDetailReq createCustomerDebtDetailReq(String customerId) {
        CustomerDebtDetailReq request = new CustomerDebtDetailReq();
        request.setClient_id(customerId);
        return request;
    }

    private CustomerDebtDetailResp sendCustomerDebtDetailReq(CustomerDebtDetailReq customerDebtDetailReq) {
        // 由 UriComponentsBuilder 统一编码一次；中文与 JSON 参数直接传原始值，不再手动 URLEncoder
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_ORDER_DETAIL_URL).queryParam("app_pid", customerDebtDetailReq.getApp_pid()).queryParam("client_id", customerDebtDetailReq.getClient_id()).queryParam("key", customerDebtDetailReq.getKey()).queryParam("lang", customerDebtDetailReq.getLang()).queryParam("order_type", customerDebtDetailReq.getOrder_type()).build().encode().toUri();

        // 传 URI（而非 String）给 RestTemplate，避免 DefaultUriBuilderFactory 二次编码
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CustomerDebtDetailResp> response = restTemplate.getForEntity(uri, CustomerDebtDetailResp.class);
        return response.getBody();
    }

    private CustomerDebtOrderReq createCustomerDebtOrderReq(String customerName, Integer page, Integer pageNum) {
        CustomerDebtOrderReq request = new CustomerDebtOrderReq();
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

    private CustomerDebtOrderResp sendCustomerDebtOrderReq(CustomerDebtOrderReq customerDebtOrderReq) {
        // 由 UriComponentsBuilder 统一编码一次；中文与 JSON 参数直接传原始值，不再手动 URLEncoder
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_ORDER_URL).queryParam("bi_key", customerDebtOrderReq.getBi_key()).queryParam("keyword", customerDebtOrderReq.getKeyword()).queryParam("page", customerDebtOrderReq.getPage()).queryParam("page_num", customerDebtOrderReq.getPage_num()).queryParam("type", customerDebtOrderReq.getType()).queryParam("sday", customerDebtOrderReq.getSday()).queryParam("eday", customerDebtOrderReq.getEday()).queryParam("order_type", customerDebtOrderReq.getOrder_type()).queryParam("pay_status", customerDebtOrderReq.getPay_status()).queryParam("lang", customerDebtOrderReq.getLang()).queryParam("key", customerDebtOrderReq.getKey()).build().encode().toUri();

        // 传 URI（而非 String）给 RestTemplate，避免 DefaultUriBuilderFactory 二次编码
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CustomerDebtOrderResp> response = restTemplate.getForEntity(uri, CustomerDebtOrderResp.class);
        return response.getBody();
    }

}