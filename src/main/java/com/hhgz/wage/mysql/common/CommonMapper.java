package com.hhgz.wage.mysql.common;

/**
 * 公共Mapper接口
 *
 * @param <T>
 */
public interface CommonMapper<T> extends CommonExtQueryMapper<T>, CommonExtUpdateMapper<T>, CommonExtDeleteMapper<T> {

}
