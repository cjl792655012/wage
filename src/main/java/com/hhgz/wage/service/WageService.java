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
        //设置为204%缩放
        sheet.setZoom(190);
        //设置列宽度为12个字符
        sheet.setColumnWidth(5, 10 * 256);
        sheet.setColumnWidth(6, 10 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        // 创建居右样式
        CellStyle rightAlignStyle = workbook.createCellStyle();
        rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightAlignStyle.setBorderTop(BorderStyle.THIN);         // 上边框
        rightAlignStyle.setBorderBottom(BorderStyle.THIN);      // 下边框
        rightAlignStyle.setBorderLeft(BorderStyle.THIN);        // 左边框
        rightAlignStyle.setBorderRight(BorderStyle.THIN);       // 右边框
        // 创建样式并设置背景色
        CellStyle colorStyle = workbook.createCellStyle();
        colorStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex()); // 设置前景色（填充色）
        colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式
        colorStyle.setBorderTop(BorderStyle.THIN);         // 上边框
        colorStyle.setBorderBottom(BorderStyle.THIN);      // 下边框
        colorStyle.setBorderLeft(BorderStyle.THIN);        // 左边框
        colorStyle.setBorderRight(BorderStyle.THIN);       // 右边框
        // 创建带全边框的样式
        CellStyle borderStyle = workbook.createCellStyle();
        // 设置边框样式和颜色
        borderStyle.setBorderTop(BorderStyle.THIN);         // 上边框
        borderStyle.setBorderBottom(BorderStyle.THIN);      // 下边框
        borderStyle.setBorderLeft(BorderStyle.THIN);        // 左边框
        borderStyle.setBorderRight(BorderStyle.THIN);       // 右边框

        // 创建一行并在其上写入一些数据
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("日期");
        headerRow.createCell(1).setCellValue("姓名");
        headerRow.createCell(2).setCellValue("总工资");
        headerRow.createCell(3).setCellValue("基本工资");
        headerRow.createCell(4).setCellValue("请假天数");
        headerRow.createCell(5).setCellValue("请假小时数");
        headerRow.createCell(6).setCellValue("请假分钟数");
        headerRow.createCell(7).setCellValue("有效工作日系数");
        headerRow.createCell(8).setCellValue("工资");
        headerRow.createCell(9).setCellValue("高温补贴");
        headerRow.createCell(10).setCellValue("餐补");
        headerRow.createCell(11).setCellValue("全勤奖");


        for (int i = 0; i < wages.size(); i++) {
            WageDTO dto = wages.get(i);
            String name = dto.getName();
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(month);
            dataRow.createCell(1).setCellValue(name);
            dataRow.createCell(2).setCellValue(dto.getTotalWageByA());
            dataRow.createCell(3).setCellValue(dto.getBaseWage());
            dataRow.createCell(4).setCellValue(dto.getCalDay());
            dataRow.createCell(5).setCellValue(dto.getCalHour());
            dataRow.createCell(6).setCellValue(dto.getCalMinute());
            dataRow.createCell(7).setCellValue(dto.getEffWorkDay());
            dataRow.createCell(8).setCellValue(dto.getCalWage());
            dataRow.createCell(9).setCellValue(dto.getCalHighTempFee());
            dataRow.createCell(10).setCellValue(dto.getCalLunchFeeByA());
            dataRow.createCell(11).setCellValue(dto.getFullAward());

            //创建个人工资明细标签页
            Sheet personalSheet = workbook.createSheet(name);
            createPersonalSheet(personalSheet, month, dto, rightAlignStyle, colorStyle, borderStyle);
        }

        // 将工作簿写入文件或输出流
        FileOutputStream fileOut = new FileOutputStream("wage.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }

    /**
     * 创建个人工资明细标签页
     *
     * @param sheet           标签页
     * @param month           日期
     * @param wage            工资明细
     * @param rightAlignStyle 居右样式
     * @param colorStyle      颜色样式
     * @param borderStyle     边框样式
     */
    private void createPersonalSheet(Sheet sheet, String month, WageDTO wage,
                                     CellStyle rightAlignStyle, CellStyle colorStyle, CellStyle borderStyle) {
        //第1列宽度为15个字符
        sheet.setColumnWidth(0, 15 * 256);
        //设置为325%缩放
        sheet.setZoom(325);
        //日期
        Row monthRow = sheet.createRow(0);
        monthRow.createCell(0).setCellValue("日期");
        Cell monthCell = monthRow.createCell(1);
        monthCell.setCellValue(month);
        monthCell.setCellStyle(rightAlignStyle);
        //姓名
        Row nameRow = sheet.createRow(1);
        nameRow.createCell(0).setCellValue("姓名");
        Cell nameCell = nameRow.createCell(1);
        nameCell.setCellValue(wage.getName());
        nameCell.setCellStyle(rightAlignStyle);
        //基本工资
        Row baseWageRow = sheet.createRow(2);
        Cell baseWageCell1 = baseWageRow.createCell(0);
        baseWageCell1.setCellValue("基本工资");
        baseWageCell1.setCellStyle(borderStyle);
        Cell baseWageCell2 = baseWageRow.createCell(1);
        baseWageCell2.setCellValue(wage.getBaseWage());
        baseWageCell2.setCellStyle(borderStyle);
        //请假天数
        Row calDayRow = sheet.createRow(3);
        Cell calDayCell1 = calDayRow.createCell(0);
        calDayCell1.setCellValue("请假天数");
        calDayCell1.setCellStyle(colorStyle);
        Cell calDayCell2 = calDayRow.createCell(1);
        calDayCell2.setCellValue(wage.getCalDay());
        calDayCell2.setCellStyle(colorStyle);
        //请假小时数
        Row calHourRow = sheet.createRow(4);
        Cell calHourCell1 = calHourRow.createCell(0);
        calHourCell1.setCellValue("请假小时数");
        calHourCell1.setCellStyle(colorStyle);
        Cell calHourCell2 = calHourRow.createCell(1);
        calHourCell2.setCellValue(wage.getCalHour());
        calHourCell2.setCellStyle(colorStyle);
        //请假分钟数
        Row calMinuteRow = sheet.createRow(5);
        Cell calMinuteCell1 = calMinuteRow.createCell(0);
        calMinuteCell1.setCellValue("请假分钟数");
        calMinuteCell1.setCellStyle(colorStyle);
        Cell calMinuteCell2 = calMinuteRow.createCell(1);
        calMinuteCell2.setCellValue(wage.getCalMinute());
        calMinuteCell2.setCellStyle(colorStyle);
        //有效工作日系数
        Row effWorkDayRow = sheet.createRow(6);
        Cell effWorkDayCell1 = effWorkDayRow.createCell(0);
        effWorkDayCell1.setCellValue("有效工作日系数");
        effWorkDayCell1.setCellStyle(borderStyle);
        Cell effWorkDayCell2 = effWorkDayRow.createCell(1);
        effWorkDayCell2.setCellValue(wage.getEffWorkDay());
        effWorkDayCell2.setCellStyle(borderStyle);
        //工资
        Row calWageRow = sheet.createRow(7);
        Cell calWageCell1 = calWageRow.createCell(0);
        calWageCell1.setCellValue("工资");
        calWageCell1.setCellStyle(borderStyle);
        Cell calWageCell2 = calWageRow.createCell(1);
        calWageCell2.setCellValue(wage.getCalWage());
        calWageCell2.setCellStyle(borderStyle);
        //高温补贴
        Row calHighTempFeeRow = sheet.createRow(8);
        Cell calHighTempFeeCell1 = calHighTempFeeRow.createCell(0);
        calHighTempFeeCell1.setCellValue("高温补贴");
        calHighTempFeeCell1.setCellStyle(borderStyle);
        Cell calHighTempFeeCell2 = calHighTempFeeRow.createCell(1);
        calHighTempFeeCell2.setCellValue(wage.getCalHighTempFee());
        calHighTempFeeCell2.setCellStyle(borderStyle);
        //餐补
        Row calLunchFeeRow = sheet.createRow(9);
        Cell calLunchFeeCell1 = calLunchFeeRow.createCell(0);
        calLunchFeeCell1.setCellValue("餐补");
        calLunchFeeCell1.setCellStyle(borderStyle);
        Cell calLunchFeeCell2 = calLunchFeeRow.createCell(1);
        calLunchFeeCell2.setCellValue(wage.getCalLunchFeeByA());
        calLunchFeeCell2.setCellStyle(borderStyle);
        //全勤奖
        Row fullAwardRow = sheet.createRow(10);
        Cell fullAwardCell1 = fullAwardRow.createCell(0);
        fullAwardCell1.setCellValue("全勤奖");
        fullAwardCell1.setCellStyle(borderStyle);
        Cell fullAwardCell2 = fullAwardRow.createCell(1);
        fullAwardCell2.setCellValue(wage.getFullAward());
        fullAwardCell2.setCellStyle(borderStyle);
        //总工资
        Row totalWageRow = sheet.createRow(11);
        Cell totalWageCell1 = totalWageRow.createCell(0);
        totalWageCell1.setCellValue("总工资");
        totalWageCell1.setCellStyle(borderStyle);
        Cell totalWageCell2 = totalWageRow.createCell(1);
        totalWageCell2.setCellValue(wage.getTotalWageByA());
        totalWageCell2.setCellStyle(borderStyle);
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
            if (calWageInt < 0) {
                calWageInt = 0;
            }
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
                    if (laterCell != null) {
                        if (laterCell.getCellType() == CellType.NUMERIC) {
                            lateMinutes = (int) laterCell.getNumericCellValue();
                            minute += (lateMinutes + 5);
                        } else if (laterCell.getCellType() == CellType.STRING) {
                            String lateMinutesStr = laterCell.getStringCellValue();
                            String lateMinutesStrValue = lateMinutesStr.replaceAll("[^0-9]", "");
                            if (!"".equals(lateMinutesStrValue)) {
                                lateMinutes = Integer.valueOf(lateMinutesStrValue);
                                minute += (lateMinutes + 5);
                            }
                        }
                    }
                    int earlyLeaveMinutes = 0;
                    if (earlyLeaveCell != null) {
                        if (earlyLeaveCell.getCellType() == CellType.NUMERIC) {
                            earlyLeaveMinutes = (int) earlyLeaveCell.getNumericCellValue();
                            minute += (earlyLeaveMinutes + 5);
                        } else if (earlyLeaveCell.getCellType() == CellType.STRING) {
                            String earlyLeaveMinutesStr = earlyLeaveCell.getStringCellValue();
                            String earlyLeaveMinutesStrValue = earlyLeaveMinutesStr.replaceAll("[^0-9]", "");
                            if (!"".equals(earlyLeaveMinutesStrValue)) {
                                earlyLeaveMinutes = Integer.valueOf(earlyLeaveMinutesStrValue);
                                minute += (earlyLeaveMinutes + 5);
                            }
                        }
                    }
                    if ((earlyLeaveMinutes + lateMinutes) > 210) {
                        lunchDec += 1;
                    }
                    if (absenteeism != null) {
                        if (absenteeism.getCellType() == CellType.NUMERIC) {
                            int absenteeismTimes = (int) absenteeism.getNumericCellValue();
                            if (absenteeismTimes == 1) {
                                lunchDec += 1;
                                day += 1;
                            }
                        } else if (absenteeism.getCellType() == CellType.STRING) {
                            String absenteeismTimesStr = absenteeism.getStringCellValue();
                            String absenteeismTimesStrValue = absenteeismTimesStr.replaceAll("[^0-9]", "");
                            if (!"".equals(absenteeismTimesStrValue)) {
                                int absenteeismTimes = Integer.valueOf(absenteeismTimesStrValue);
                                if (absenteeismTimes == 1) {
                                    lunchDec += 1;
                                    day += 1;
                                }
                            }
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