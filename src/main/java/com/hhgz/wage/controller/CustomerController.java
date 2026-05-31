package com.hhgz.wage.controller;

import com.hhgz.wage.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @description: 客户接口
 * @author: JinLong Cai
 * @date: 2026/5/29 22:58
 */
@RestController
@RequestMapping("customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/downloadExcel")
    public void downloadExcel() throws IOException {
        customerService.createCustomerDebtExcel();
    }

}