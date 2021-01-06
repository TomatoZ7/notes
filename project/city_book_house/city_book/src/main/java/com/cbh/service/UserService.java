package com.cbh.service;

import java.util.List;
import java.util.Map;

import com.cbh.domain.User;

public interface UserService {
	// �������������mapper�в�һ�����������Ӧ@Param("����") ע�����У�����磺@Param("id") int userid
	List<List<?>> getUserList(Map param);
	
	User getUserById(int id);
    
    int delUserById(int id);
    
    int updateUser(User user);

    int insertUser(User user);
}
