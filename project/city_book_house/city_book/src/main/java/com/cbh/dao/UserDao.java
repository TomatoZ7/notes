package com.cbh.dao;

import java.util.List;
import java.util.Map;

import com.cbh.domain.User;

public interface UserDao {
	List<List<?>> getUserList(Map param);
	
	User getUserById(int id);
	
	int verifyUser(Map param);
	
	int countUser();
}
