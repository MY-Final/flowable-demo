package com.my.flowabledemo.common;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用ID请求参数
 */
@Data
public class IdRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ID不能为空")
    private Long id;
}