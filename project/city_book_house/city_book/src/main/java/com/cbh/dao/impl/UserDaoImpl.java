package com.cbh.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbh.dao.UserDao;
import com.cbh.domain.User;

@Component
public class UserDaoImpl implements UserDao {
	@Autowired
    SqlSessionTemplate ssTemplate;

	@Override
	public List<List<?>> getUserList(Map param) {
		// TODO Auto-generated method stub
		return ssTemplate.getMapper(UserDao.class).getUserList(param);
	}
	
	@Override
	public User getUserById(int id) {
		return ssTemplate.getMapper(UserDao.class).getUserById(id);
	}
	
	@Override
	public int delUserById(int id) {
		return ssTemplate.getMapper(UserDao.class).delUserById(id);
	}
	
	@Override
	public int updateUser(User user) {
		return ssTemplate.getMapper(UserDao.class).updateUser(user);
	}
	
	@Override
	public int insertUser(User user) {
		return ssTemplate.getMapper(UserDao.class).insertUser(user);
	}
}
