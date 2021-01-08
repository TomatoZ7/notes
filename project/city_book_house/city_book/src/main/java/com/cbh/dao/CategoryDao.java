package com.cbh.dao;

import java.util.List;
import java.util.Map;

import com.cbh.domain.Category;

public interface CategoryDao {
	List<List<?>> getCategoryList(Map param);
	
	Category getCategoryById(int id);
}
