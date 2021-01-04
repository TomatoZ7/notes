package com.cbh.dao;

import java.util.Map;

import com.cbh.domain.User;

public interface UserDao {
	// �������������mapper�в�һ�����������Ӧ@Param("����") ע�����У��
    User getUserById(int id);
    
    int delUserById(int id);
    
    int updateUser(Map param);

    int insertUser(User user);
}
