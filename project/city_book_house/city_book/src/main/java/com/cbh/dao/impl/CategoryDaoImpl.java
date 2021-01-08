package com.cbh.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbh.dao.CategoryDao;
import com.cbh.domain.Category;

@Component
public class CategoryDaoImpl implements CategoryDao {
	@Autowired
    SqlSessionTemplate ssTemplate;

	@Override
	public List<List<?>> getCategoryList(Map param) {
		// TODO Auto-generated method stub
		return ssTemplate.getMapper(CategoryDao.class).getCategoryList(param);
	}
	
	@Override
	public Category getCategoryById(int id) {
		// TODO Auto-generated method stub
		return ssTemplate.getMapper(CategoryDao.class).getCategoryById(id);
	}
}
