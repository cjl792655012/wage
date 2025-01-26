package com.hhgz.wage.mysql.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public interface CommonExtDeleteMapper<T> extends BaseMapper<T> {

    /**
     * 指定1个字段作为删除的where条件
     *
     * @param column
     * @param val
     * @param <V>
     * @return
     */
    default <V> int deleteBy(SFunction<T, V> column, V val) {
        return delete(Wrappers.<T>lambdaUpdate().eq(column, val));
    }

    /**
     * 指定2个字段作为删除的where条件
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param <V1>
     * @param <V2>
     * @return
     */
    default <V1, V2> int deleteBy(SFunction<T, V1> column1, V1 val1,
                                  SFunction<T, V2> column2, V2 val2) {
        return delete(Wrappers.<T>lambdaUpdate()
                .eq(column1, val1)
                .eq(column2, val2)
        );
    }

}
