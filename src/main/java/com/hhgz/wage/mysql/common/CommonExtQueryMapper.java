package com.hhgz.wage.mysql.common;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.annotations.Param;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface CommonExtQueryMapper<T> extends BaseMapper<T> {

    /**
     * 查询全部记录
     * <p color="red">仅当数据量较小时使用, 当业务数量规模和增长无法预估时, 请勿使用</p>
     *
     * @return List<T>
     */
    default List<T> selectAll() {
        return selectList(null);
    }

    /**
     * 根据 entity 条件，查询一条记录, 若where条件为空, 则返回null
     * <p>查询一条记录，例如 qw.last("limit 1") 限制取一条记录, 注意：多条数据会报异常</p>
     *
     * @param queryWrapper 实体对象封装操作类（entity 条件不可以为 null, 需标明）
     * @return T
     */
    default T selectOneByNotEmptyOfWhere(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        if (queryWrapper.isEmptyOfWhere()) {
            return null;
        }
        return selectOne(queryWrapper);
    }

    /**
     * 根据 entity 条件，查询全部记录, 若where条件为空, 则返回空集
     *
     * @param queryWrapper 实体对象封装操作类（entity 条件不可以为 null, 需标明）
     * @return List<T>
     */
    default List<T> selectListByNotEmptyOfWhere(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        if (queryWrapper.isEmptyOfWhere()) {
            return Collections.emptyList();
        }
        return selectList(queryWrapper);
    }

    /**
     * 指定1个字段作为查询的where条件
     *
     * @param column
     * @param val
     * @param <V>
     * @return
     */
    default <V> List<T> selectListBy(SFunction<T, V> column, V val) {
        return selectList(Wrappers.<T>lambdaQuery().eq(column, val));
    }

    /**
     * 指定2个字段作为查询的where条件
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param <V1>
     * @param <V2>
     * @return
     */
    default <V1, V2> List<T> selectListBy(SFunction<T, V1> column1, V1 val1,
                                          SFunction<T, V2> column2, V2 val2) {
        return selectList(Wrappers.<T>lambdaQuery()
                .eq(column1, val1)
                .eq(column2, val2)
        );
    }

    /**
     * 指定3个字段作为查询的where条件
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param column3
     * @param val3
     * @param <V1>
     * @param <V2>
     * @param <V3>
     * @return
     */
    default <V1, V2, V3> List<T> selectListBy(SFunction<T, V1> column1, V1 val1,
                                              SFunction<T, V2> column2, V2 val2,
                                              SFunction<T, V3> column3, V3 val3) {
        return selectList(Wrappers.<T>lambdaQuery()
                .eq(column1, val1)
                .eq(column2, val2)
                .eq(column3, val3)
        );
    }

    /**
     * 指定1个字段作为查询的where条件
     *
     * @param column
     * @param val
     * @param <V>
     * @return
     */
    default <V> T selectOneBy(SFunction<T, V> column, V val) {
        return selectOne(Wrappers.<T>lambdaQuery().eq(column, val));
    }

    /**
     * 指定2个字段作为查询的where条件
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param <V1>
     * @param <V2>
     * @return
     */
    default <V1, V2> T selectOneBy(SFunction<T, V1> column1, V1 val1,
                                   SFunction<T, V2> column2, V2 val2) {
        return selectOne(Wrappers.<T>lambdaQuery()
                .eq(column1, val1)
                .eq(column2, val2)
        );
    }

    /**
     * 指定3个字段作为查询的where条件
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param column3
     * @param val3
     * @param <V1>
     * @param <V2>
     * @param <V3>
     * @return
     */
    default <V1, V2, V3> T selectOneBy(SFunction<T, V1> column1, V1 val1,
                                       SFunction<T, V2> column2, V2 val2,
                                       SFunction<T, V3> column3, V3 val3) {
        return selectOne(Wrappers.<T>lambdaQuery()
                .eq(column1, val1)
                .eq(column2, val2)
                .eq(column3, val3)
        );
    }

    /**
     * 指定1个字段作为查询的where条件, 返回 Optional
     *
     * @param column
     * @param val
     * @param <V>
     * @return
     */
    default <V> Optional<T> selectOneOptBy(SFunction<T, V> column, V val) {
        return Optional.ofNullable(selectOne(Wrappers.<T>lambdaQuery().eq(column, val)));
    }

    /**
     * 指定2个字段作为查询的where条件, 返回 Optional
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param <V1>
     * @param <V2>
     * @return
     */
    default <V1, V2> Optional<T> selectOneOptBy(SFunction<T, V1> column1, V1 val1,
                                                SFunction<T, V2> column2, V2 val2) {
        return Optional.ofNullable(selectOne(Wrappers.<T>lambdaQuery()
                .eq(column1, val1)
                .eq(column2, val2)
        ));
    }

    /**
     * 指定3个字段作为查询的where条件, 返回 Optional
     *
     * @param column1
     * @param val1
     * @param column2
     * @param val2
     * @param column3
     * @param val3
     * @param <V1>
     * @param <V2>
     * @param <V3>
     * @return
     */
    default <V1, V2, V3> Optional<T> selectOneOptBy(SFunction<T, V1> column1, V1 val1,
                                                    SFunction<T, V2> column2, V2 val2,
                                                    SFunction<T, V3> column3, V3 val3) {
        return Optional.ofNullable(selectOne(Wrappers.<T>lambdaQuery()
                .eq(column1, val1)
                .eq(column2, val2)
                .eq(column3, val3)
        ));
    }

}
