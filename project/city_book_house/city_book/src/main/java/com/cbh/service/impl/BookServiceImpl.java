package com.cbh.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbh.dao.BookDao;
import com.cbh.domain.Book;
import com.cbh.service.BookService;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	BookDao bookDao;
	
	@Override
	public List<List<?>> getBookList(Map param){
		return bookDao.getBookList(param);
	}
	
	@Override
	public Book getBookById(int id) {
		return bookDao.getBookById(id);
	}
	
	@Override
	public int insertBook(Book book) {
		return bookDao.insertBook(book);
	}
	
	@Override
	public int updateBook(Book book) {
		return bookDao.updateBook(book);
	}
	
	@Override
	public int delBookById(int id) {
		return bookDao.delBookById(id);
	}
	
	@Override
	public int countBook() {
		return bookDao.countBook();
	}
	
	@Override
	public List<Object> countBookByCategory(){
		return bookDao.countBookByCategory();
	}
}
