package com.hhgz.wage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.hhgz.wage.mysql.mapper"})
@SpringBootApplication(scanBasePackages = {"com.hhgz.wage.*"})
public class WageApplication {

    public static void main(String[] args) {
        SpringApplication.run(WageApplication.class, args);
    }

}
