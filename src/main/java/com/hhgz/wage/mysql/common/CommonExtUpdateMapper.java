package com.hhgz.wage.mysql.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public interface CommonExtUpdateMapper<T> extends BaseMapper<T> {

    /**
     * 指定1个字段作为更新的where条件
     *
     * @param e      更新实体
     * @param column where 条件列
     * @param val    where 条件值
     * @return
     */
    default <V> int updateBy(T e, SFunction<T, V> column, V val) {
        return update(e, Wrappers.<T>lambdaUpdate().eq(column, val));
    }

    /**
     * 指定2个字段作为更新的where条件
     *
     * @param e
     * @param column1
     * @param val1
     * @param e
     * @param column2
     * @param val2
     * @param <V1>
     * @param <V2>
     * @return
     */
    default <V1, V2> int updateBy(T e,
                                  SFunction<T, V1> column1, V1 val1,
                                  SFunction<T, V1> column2, V1 val2) {
        return update(e, Wrappers.<T>lambdaUpdate()
                .eq(column1, val1)
                .eq(column2, val2)
        );
    }

}
