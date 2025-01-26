package com.hhgz.wage.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hhgz.wage.form.AttendanceTimeSaveForm;
import com.hhgz.wage.service.WageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/7/6 14:31
 */
@RestController
@RequestMapping("wage")
@RequiredArgsConstructor
public class WageController {

    private final WageService wageService;

    @PostMapping("/saveAttendanceTime")
    public void saveAttendanceTime(@RequestBody @Valid List<AttendanceTimeSaveForm> forms) {
        wageService.saveAttendanceTime(forms);
    }

    @PostMapping("/upload")
    public String uploadExcelFile(@RequestParam("file") MultipartFile file) {
        return wageService.readExcel(file);
    }

    @GetMapping("/downloadExcel")
    public ResponseEntity<Resource> downloadExcel(@RequestParam("date") String date) throws IOException {
        DateTime dateTime = DateUtil.parse(date, DatePattern.SIMPLE_MONTH_PATTERN);
        // 生成Excel文件
        wageService.createExcel(dateTime.toJdkDate());
        // 构造文件路径
        String filePath = "wage.xlsx";
        Path path = Paths.get(filePath);
        FileSystemResource resource = new FileSystemResource(path);
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wage.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

}