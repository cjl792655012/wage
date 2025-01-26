package com.hhgz.wage.mysql.repository;

import com.hhgz.wage.mysql.mapper.PersonMapper;
import com.hhgz.wage.mysql.table.Person;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/7/6 13:51
 */
@Repository
public class PersonRepository {

    @Resource
    private PersonMapper personMapper;

    public List<Person> selectAll() {
        return personMapper.selectAll();
    }

}