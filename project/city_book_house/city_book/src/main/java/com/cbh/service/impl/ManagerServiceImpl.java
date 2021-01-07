package com.cbh.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbh.dao.ManagerDao;
import com.cbh.domain.Manager;
import com.cbh.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {
	@Autowired
    ManagerDao managerDao;

	@Override
	public List<List<?>> getManagerList(Map param) {
		return managerDao.getManagerList(param);
	}
	
	@Override
	public Manager getManagerById(int id) {
		return managerDao.getManagerById(id);
	}
	
	@Override
	public int delManagerById(int id) {
		return managerDao.delManagerById(id);
	}
	
	@Override
	public int updateManager(Manager Manager) {
		return managerDao.updateManager(Manager);
	}
	
	@Override
	public int insertManager(Manager Manager) {
		return managerDao.insertManager(Manager);
	}
}
