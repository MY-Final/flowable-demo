package com.my.flowabledemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.flowabledemo.pojo.FormDefinition;
import com.my.flowabledemo.service.FormDefinitionService;
import com.my.flowabledemo.mapper.FormDefinitionMapper;
import org.springframework.stereotype.Service;

/**
* @author 34861
* @description 针对表【form_definition(表单定义表)】的数据库操作Service实现
* @createDate 2026-06-07 10:36:12
*/
@Service
public class FormDefinitionServiceImpl extends ServiceImpl<FormDefinitionMapper, FormDefinition>
    implements FormDefinitionService{

}




