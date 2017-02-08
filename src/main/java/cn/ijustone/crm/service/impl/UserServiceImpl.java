package cn.ijustone.crm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.ijustone.crm.domain.User;
import cn.ijustone.crm.mapper.UserMapper;
import cn.ijustone.crm.service.IUserService;

@Service("userService")  
public class UserServiceImpl implements IUserService {  
    @Resource  
    private UserMapper userMapper;  
    
    public User getUserById(Long userId) {  
        // TODO Auto-generated method stub  
        return this.userMapper.queryOne(userId);  
    }  
    public User getAll(Long userId) {  
    	// TODO Auto-generated method stub  
    	return this.userMapper.queryOne(userId);  
    }  
  
} 