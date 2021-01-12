package com.cbh.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbh.dao.UserDao;
import com.cbh.domain.User;
import com.cbh.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	UserDao userDao;
	
	@Override
	public List<List<?>> getUserList(Map param) {
		return userDao.getUserList(param);
	}
	
	@Override
	public User getUserById(int id) {
		return userDao.getUserById(id);
	}
}
