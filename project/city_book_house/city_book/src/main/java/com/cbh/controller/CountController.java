package com.cbh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.service.BookService;
import com.cbh.service.CategoryService;
import com.cbh.service.ManagerService;
import com.cbh.service.UserService;

@RequestMapping(value = "/count")
@RestController
public class CountController {
	@Autowired
    CategoryService categoryService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ManagerService managerService;
	
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public HashMap<?,?> count() {
		
		int cate = categoryService.countCategory();
		int book = bookService.countBook();
		int user = userService.countUser();
		int mana = managerService.countManager();
		
		List<Object> countBookByCate = bookService.countBookByCategory();
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("cate_count", cate);
		res.put("book_count", book);
		res.put("user_count", user);
		res.put("manager_count", mana);
		res.put("book_category", countBookByCate);
		return res;
	}
}
