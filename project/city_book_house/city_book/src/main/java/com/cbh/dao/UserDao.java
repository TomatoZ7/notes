package com.cbh.dao;

import java.util.List;
import java.util.Map;

import com.cbh.domain.User;

public interface UserDao {
	// �������������mapper�в�һ�����������Ӧ@Param("����") ע�����У��
	List<List<?>> getUserList(Map param);
	
    User getUserById(int id);
    
    int delUserById(int id);
    
    int updateUser(User user);

    int insertUser(User user);
}
