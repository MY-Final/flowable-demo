package com.my.flowabledemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.flowabledemo.pojo.ProcessForm;
import com.my.flowabledemo.service.ProcessFormService;
import com.my.flowabledemo.mapper.ProcessFormMapper;
import org.springframework.stereotype.Service;

/**
* @author 34861
* @description 针对表【process_form(流程表单关联表)】的数据库操作Service实现
* @createDate 2026-06-07 10:36:12
*/
@Service
public class ProcessFormServiceImpl extends ServiceImpl<ProcessFormMapper, ProcessForm>
    implements ProcessFormService{

}




