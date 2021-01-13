package com.cbh.dao;

import java.util.List;
import java.util.Map;

import com.cbh.domain.Book;

public interface BookDao {
	List<List<?>> getBookList(Map param);
	
	Book getBookById(int id);
	
	int insertBook(Book book);
	
	int updateBook(Book book);
	
	int delBookById(int id);
	
	int countBook();
	
	List<Object> countBookByCategory(); 
}
