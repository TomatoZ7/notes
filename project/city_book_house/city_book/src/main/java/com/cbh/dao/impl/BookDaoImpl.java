package com.cbh.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbh.dao.BookDao;
import com.cbh.domain.Book;

@Component
public class BookDaoImpl implements BookDao {
	@Autowired
    SqlSessionTemplate ssTemplate;
	
	@Override
	public List<List<?>> getBookList(Map param){
		return ssTemplate.getMapper(BookDao.class).getBookList(param);
	}
	
	@Override
	public Book getBookById(int id) {
		return ssTemplate.getMapper(BookDao.class).getBookById(id);
	}
	
	@Override
	public int insertBook(Book book) {
		return ssTemplate.getMapper(BookDao.class).insertBook(book);
	}
	
	@Override
	public int updateBook(Book book) {
		return ssTemplate.getMapper(BookDao.class).updateBook(book);
	}
	
	@Override
	public int delBookById(int id) {
		return ssTemplate.getMapper(BookDao.class).delBookById(id);
	}
}
