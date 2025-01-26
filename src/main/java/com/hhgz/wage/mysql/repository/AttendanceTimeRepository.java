package com.hhgz.wage.mysql.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhgz.wage.mysql.mapper.AttendanceTimeMapper;
import com.hhgz.wage.mysql.table.AttendanceTime;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/7/6 13:51
 */
@Repository
public class AttendanceTimeRepository {

    @Resource
    private AttendanceTimeMapper attendanceTimeMapper;

    public void insert(AttendanceTime record) {
        record.setInsertTime(new Date());
        attendanceTimeMapper.insert(record);
    }

    public AttendanceTime selectOneByNameAndDate(String name) {
        return attendanceTimeMapper.selectOne(new LambdaQueryWrapper<AttendanceTime>()
                .eq(AttendanceTime::getName, name)
        );
    }

}