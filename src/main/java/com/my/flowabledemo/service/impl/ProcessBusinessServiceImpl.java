package com.my.flowabledemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.flowabledemo.pojo.ProcessBusiness;
import com.my.flowabledemo.service.ProcessBusinessService;
import com.my.flowabledemo.mapper.ProcessBusinessMapper;
import org.springframework.stereotype.Service;

/**
* @author 34861
* @description 针对表【process_business(流程业务数据关联表)】的数据库操作Service实现
* @createDate 2026-06-07 10:36:12
*/
@Service
public class ProcessBusinessServiceImpl extends ServiceImpl<ProcessBusinessMapper, ProcessBusiness>
    implements ProcessBusinessService{

}




