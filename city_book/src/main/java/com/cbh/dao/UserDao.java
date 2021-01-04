package com.cbh.dao;

import java.util.Map;

import com.cbh.domain.User;

public interface UserDao {
	// 对于其参数名与mapper中不一致情况，可适应@Param("名称") 注解进行校正
    User getUserById(int id);
    
    int delUserById(int id);
    
    int updateUser(Map param);

    int insertUser(User user);
}
