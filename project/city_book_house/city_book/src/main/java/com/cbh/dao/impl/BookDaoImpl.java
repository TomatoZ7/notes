package com.cbh.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbh.dao.BookDao;

@Component
public class BookDaoImpl implements BookDao {
	@Autowired
    SqlSessionTemplate ssTemplate;
	
	@Override
	public List<List<?>> getBookList(Map param){
		return ssTemplate.getMapper(BookDao.class).getBookList(param);
	}
}
