package com.my.flowabledemo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码，默认1 */
    private int pageNum = 1;

    /** 每页数量，默认10 */
    private int pageSize = 10;

    /** 排序字段 */
    private String orderBy;

    /** 是否升序，默认false */
    private boolean asc = false;

    /**
     * 获取偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}