package com.cbh.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbh.dao.ManagerDao;
import com.cbh.domain.Manager;

@Component
public class ManagerDaoImpl implements ManagerDao {
	@Autowired
    SqlSessionTemplate ssTemplate;

	@Override
	public List<List<?>> getManagerList(Map param) {
		// TODO Auto-generated method stub
		return ssTemplate.getMapper(ManagerDao.class).getManagerList(param);
	}
	
	@Override
	public Manager getManagerById(int id) {
		return ssTemplate.getMapper(ManagerDao.class).getManagerById(id);
	}
	
	@Override
	public int delManagerById(int id) {
		return ssTemplate.getMapper(ManagerDao.class).delManagerById(id);
	}
	
	@Override
	public int updateManager(Manager Manager) {
		return ssTemplate.getMapper(ManagerDao.class).updateManager(Manager);
	}
	
	@Override
	public int insertManager(Manager Manager) {
		return ssTemplate.getMapper(ManagerDao.class).insertManager(Manager);
	}
	
	@Override
	public int countManager() {
		return ssTemplate.getMapper(ManagerDao.class).countManager();
	}
	
	@Override
	public Manager getManagerByLogin(Manager manager) {
		return ssTemplate.getMapper(ManagerDao.class).getManagerByLogin(manager);
	}
}
