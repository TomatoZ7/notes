package com.cbh.service;

import java.util.List;
import java.util.Map;

import com.cbh.domain.User;

public interface UserService {
	List<List<?>> getUserList(Map param);
	
	User getUserById(int id);
}
