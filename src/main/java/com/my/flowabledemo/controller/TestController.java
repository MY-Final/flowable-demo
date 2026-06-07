package com.my.flowabledemo.controller;

import com.my.flowabledemo.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "测试接口")
@RestController
public class TestController {

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "Service is running");
        return R.ok(result);
    }

    @Operation(summary = "Hello接口")
    @GetMapping("/hello")
    public R<Map<String, Object>> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Hello, World!");
        return R.ok(result);
    }
}