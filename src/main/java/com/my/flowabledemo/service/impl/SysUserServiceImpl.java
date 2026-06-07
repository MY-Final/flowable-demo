package com.my.flowabledemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.flowabledemo.pojo.SysUser;
import com.my.flowabledemo.service.SysUserService;
import com.my.flowabledemo.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
* @author 34861
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2026-06-07 10:36:12
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

}




