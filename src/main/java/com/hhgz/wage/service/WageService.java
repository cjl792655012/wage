package com.hhgz.wage.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Month;
import com.hhgz.wage.dto.PersonTimeDTO;
import com.hhgz.wage.dto.WageDTO;
import com.hhgz.wage.form.AttendanceTimeSaveForm;
import com.hhgz.wage.mysql.repository.AttendanceTimeRepository;
import com.hhgz.wage.mysql.repository.PersonRepository;
import com.hhgz.wage.mysql.table.AttendanceTime;
import com.hhgz.wage.mysql.table.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/7/6 14:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WageService {

    private final PersonRepository personRepository;

    private final AttendanceTimeRepository attendanceTimeRepository;

    @Transactional(rollbackFor = Exception.class)
    public void saveAttendanceTime(List<AttendanceTimeSaveForm> forms) {
        if (CollectionUtil.isEmpty(forms)) {
            return;
        }
        forms.forEach(form -> {
            AttendanceTime attendanceTime = new AttendanceTime();
            BeanUtil.copyProperties(form, attendanceTime);
            attendanceTimeRepository.insert(attendanceTime);
        });
    }

    public void createExcel(Date date) throws IOException {

        List<WageDTO> wages = buildWages(date);

        Workbook workbook = new XSSFWorkbook(); // 使用XSSFWorkbook创建一个.xlsx格式的工作簿
        String month = DateUtil.format(date, DatePattern.NORM_MONTH_PATTERN);
        Sheet sheet = workbook.createSheet(month);

        // 创建一行并在其上写入一些数据
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("日期");
        headerRow.createCell(1).setCellValue("姓名");
        headerRow.createCell(2).setCellValue("基本工资");
        headerRow.createCell(3).setCellValue("请假天数");
        headerRow.createCell(4).setCellValue("请假小时数");
        headerRow.createCell(5).setCellValue("请假分钟数");
        headerRow.createCell(6).setCellValue("有效工作日系数");
        headerRow.createCell(7).setCellValue("工资");
        headerRow.createCell(8).setCellValue("高温补贴");
        /*headerRow.createCell(9).setCellValue("餐补");
        headerRow.createCell(10).setCellValue("餐补X");
        headerRow.createCell(11).setCellValue("餐补A");
        headerRow.createCell(12).setCellValue("全勤奖");
        headerRow.createCell(13).setCellValue("总工资");
        headerRow.createCell(14).setCellValue("总工资X");
        headerRow.createCell(15).setCellValue("总工资A");*/
        headerRow.createCell(9).setCellValue("餐补");
        headerRow.createCell(10).setCellValue("全勤奖");
        headerRow.createCell(11).setCellValue("总工资");

        for (int i = 0; i < wages.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(month);
            dataRow.createCell(1).setCellValue(wages.get(i).getName());
            dataRow.createCell(2).setCellValue(wages.get(i).getBaseWage());
            dataRow.createCell(3).setCellValue(wages.get(i).getCalDay());
            dataRow.createCell(4).setCellValue(wages.get(i).getCalHour());
            dataRow.createCell(5).setCellValue(wages.get(i).getCalMinute());
            dataRow.createCell(6).setCellValue(wages.get(i).getEffWorkDay());
            dataRow.createCell(7).setCellValue(wages.get(i).getCalWage());
            dataRow.createCell(8).setCellValue(wages.get(i).getCalHighTempFee());
            /*dataRow.createCell(9).setCellValue(wages.get(i).getCalLunchFee());
            dataRow.createCell(10).setCellValue(wages.get(i).getCalLunchFeeByX());
            dataRow.createCell(11).setCellValue(wages.get(i).getCalLunchFeeByA());
            dataRow.createCell(12).setCellValue(wages.get(i).getFullAward());
            dataRow.createCell(13).setCellValue(wages.get(i).getTotalWage());
            dataRow.createCell(14).setCellValue(wages.get(i).getTotalWageByX());
            dataRow.createCell(15).setCellValue(wages.get(i).getTotalWageByA());*/
            dataRow.createCell(9).setCellValue(wages.get(i).getCalLunchFeeByA());
            dataRow.createCell(10).setCellValue(wages.get(i).getFullAward());
            dataRow.createCell(11).setCellValue(wages.get(i).getTotalWageByA());
        }

        // 将工作簿写入文件或输出流
        FileOutputStream fileOut = new FileOutputStream("wage.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }

    private List<WageDTO> buildWages(Date date) {
        //月份
        Month month = DateUtil.monthEnum(date);
        //当月天数
        final int lastDay = month.getLastDay(DateUtil.isLeapYear(DateUtil.year(date)));
        List<WageDTO> wages = new ArrayList<>();
        List<Person> persons = personRepository.selectAll();
        persons.forEach(person -> {
            AttendanceTime att = attendanceTimeRepository.selectOneByNameAndDate(person.getName());

            //基本工资
            BigDecimal baseWage = new BigDecimal(person.getBaseWage());
            //计算请假信息
            Integer calDay = att.getDay() + att.getMinute() / (10 * 60);
            Integer calHour = (att.getMinute() % (10 * 60)) / 60;
            Integer calMinute = att.getMinute() % 60;
            //确定有效工作日系数
            Integer effWorkDay = 32;
            if (calDay == 4) {
                effWorkDay = 31;
            } else if (calDay >= 5) {
                effWorkDay = 30;
            }
            Integer effWorkCoe = effWorkDay * 10 * 60;
            //每月总分钟数
            BigDecimal totalMonthMinutes = new BigDecimal(30 * 10 * 60);
            //计算工资
            BigDecimal calWage = baseWage.divide(totalMonthMinutes, 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(effWorkCoe - att.getDay() * 10 * 60 - att.getMinute()));
            //计算餐补
            Integer calLunchIncDay = 0;
            if (calHour >= 4) {
                calLunchIncDay += 1;
            }
            Integer calLunchFeeByA = lastDay * 10 - (calDay + calLunchIncDay) * 10;
            //计算高温补贴
            BigDecimal calHighTempFee = new BigDecimal(0);
            final int monthInt = DateUtil.month(date) + 1;

            if (monthInt == 6 || monthInt == 7 || monthInt == 8) {
                Double calHighTempFeeDouble = (double) 100 / (double) lastDay * ((double) lastDay - (double) calDay - (double) calHour / 10);
                calHighTempFee = new BigDecimal(calHighTempFeeDouble);
            }
            Integer calWageInt = calWage.setScale(0, RoundingMode.HALF_UP).intValue();
            Integer calHighTempFeeInt = calHighTempFee.setScale(0, RoundingMode.HALF_UP).intValue();

            Integer fullAward = 0;
            if (calDay == 0 && calHour == 0 && calMinute == 0) {
                fullAward = 100;
            }

            //总工资
            Integer totalWageByA = calWageInt + calLunchFeeByA + calHighTempFeeInt + fullAward;

            WageDTO dto = new WageDTO();
            dto.setName(person.getName());
            dto.setBaseWage(baseWage.intValue());
            dto.setCalDay(calDay);
            dto.setCalHour(calHour);
            dto.setCalMinute(calMinute);
            dto.setEffWorkDay(effWorkDay);
            dto.setCalWage(calWageInt);
            dto.setCalHighTempFee(calHighTempFeeInt);
            dto.setCalLunchFeeByA(calLunchFeeByA);
            dto.setFullAward(fullAward);
            dto.setTotalWageByA(totalWageByA);
            wages.add(dto);
        });

        return wages;
    }

    public String readExcel(MultipartFile file) {
        Map<String, PersonTimeDTO> result = new HashMap<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            //数据在第一张表上
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                //跳过表头
                if (row.getRowNum() <= 3) continue;
                //姓名在第2列（索引为1）
                Cell nameCell = row.getCell(1);
                //迟到时长在第18列（索引为17）
                Cell laterCell = row.getCell(17);
                //早退时长在第20列（索引为19）
                Cell earlyLeaveCell = row.getCell(19);
                //旷工次数在第21列（索引为20）
                Cell absenteeism = row.getCell(20);


                if (nameCell != null) {
                    String name = nameCell.getStringCellValue();
                    PersonTimeDTO thePersonTime = result.get(name);
                    if (thePersonTime == null) {
                        thePersonTime = new PersonTimeDTO();
                    }
                    Integer day = thePersonTime.getDay();
                    Integer minute = thePersonTime.getMinute();
                    Integer lunchDec = thePersonTime.getLunchDec();

                    int lateMinutes = 0;
                    if (laterCell != null && laterCell.getCellType() == CellType.NUMERIC) {
                        lateMinutes = (int) laterCell.getNumericCellValue();
                        minute += (lateMinutes + 5);
                    }
                    int earlyLeaveMinutes = 0;
                    if (earlyLeaveCell != null && earlyLeaveCell.getCellType() == CellType.NUMERIC) {
                        earlyLeaveMinutes = (int) earlyLeaveCell.getNumericCellValue();
                        minute += (earlyLeaveMinutes + 5);
                    }
                    if ((earlyLeaveMinutes + lateMinutes) > 210) {
                        lunchDec += 1;
                    }
                    if (absenteeism != null && absenteeism.getCellType() == CellType.NUMERIC) {
                        int absenteeismTimes = (int) absenteeism.getNumericCellValue();
                        if (absenteeismTimes == 1) {
                            lunchDec += 1;
                            day += 1;
                        }
                    }
                    thePersonTime.setDay(day);
                    thePersonTime.setMinute(minute);
                    thePersonTime.setLunchDec(lunchDec);
                    result.put(name, thePersonTime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process the file!";
        }
        result.forEach((name, thePersonTime) -> {
            System.out.println(name + " :请假天数--- " + thePersonTime.getDay() + " 天，请假时长--- " + thePersonTime.getMinute() + " 分钟，午餐扣减 " + thePersonTime.getLunchDec() + " 次，");
        });

        return "File uploaded and processed successfully!";
    }

}