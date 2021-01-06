package com.cbh.service;

import java.util.List;
import java.util.Map;

import com.cbh.domain.User;

public interface UserService {
	// 对于其参数名与mapper中不一致情况，可适应@Param("名称") 注解进行校正，如：@Param("id") int userid
	List<List<?>> getUserList(Map param);
	
	User getUserById(int id);
    
    int delUserById(int id);
    
    int updateUser(User user);

    int insertUser(User user);
}
