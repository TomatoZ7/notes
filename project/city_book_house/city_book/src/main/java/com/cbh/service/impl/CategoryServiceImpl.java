package com.cbh.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbh.dao.CategoryDao;
import com.cbh.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
    CategoryDao categoryDao;
	
	@Override
	public List<List<?>> getCategoryList(Map param) {
		return categoryDao.getCategoryList(param);
	}
}
