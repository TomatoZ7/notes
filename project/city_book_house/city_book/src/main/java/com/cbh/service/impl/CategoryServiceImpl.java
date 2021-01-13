package com.cbh.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbh.dao.CategoryDao;
import com.cbh.domain.Category;
import com.cbh.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
    CategoryDao categoryDao;
	
	@Override
	public List<List<?>> getCategoryList(Map param) {
		return categoryDao.getCategoryList(param);
	}
	
	@Override
	public Category getCategoryByid(int id) {
		return categoryDao.getCategoryById(id);
	}
	
	@Override
	public int insertCategory(Category category) {
		return categoryDao.insertCategory(category);
	}
	
	@Override
	public Category getCategoryByName(String category_name) {
		return categoryDao.getCategoryByName(category_name);
	}
	
	@Override
	public int updateCategory(Category category) {
		return categoryDao.updateCategory(category);
	}
	
	@Override
	public int delCategoryById(int id) {
		return categoryDao.delCategoryById(id);
	}
	
	@Override
	public List<List<?>> getCategoryWithLevel(){
		return categoryDao.getCategoryWithLevel();
	}
	
	@Override
	public int countCategory() {
		return categoryDao.countCategory();
	}
}
