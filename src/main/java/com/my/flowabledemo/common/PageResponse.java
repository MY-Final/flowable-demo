package com.my.flowabledemo.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应结果
 */
@Data
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页数据 */
    private List<T> records;

    /** 当前页码 */
    private int pageNum;

    /** 每页数量 */
    private int pageSize;

    /** 总页数 */
    private int pages;

    public PageResponse() {
        this.records = Collections.emptyList();
    }

    public PageResponse(long total, List<T> records, int pageNum, int pageSize) {
        this.total = total;
        this.records = records;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    /**
     * 创建空的分页响应
     */
    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(0, Collections.emptyList(), 1, 10);
    }

    /**
     * 创建分页响应
     */
    public static <T> PageResponse<T> of(long total, List<T> records, int pageNum, int pageSize) {
        return new PageResponse<>(total, records, pageNum, pageSize);
    }
}