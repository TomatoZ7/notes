package com.cbh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.service.BookService;

@RequestMapping(value = "/book")
@RestController
public class BookController {
	@Autowired
	BookService bookservice;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(HttpServletRequest request) {
		Map<String,Object> param = new HashMap<>();
		param.put("offset", request.getParameter("offset"));
		param.put("limit", request.getParameter("limit"));
		param.put("limit", request.getParameter("first_category_id"));
		param.put("limit", request.getParameter("second_category_id"));
		param.put("limit", request.getParameter("book_name"));
		param.put("limit", request.getParameter("status"));
		param.put("limit", request.getParameter("content"));
		
		List<List<?>> list = bookservice.getBookList(param);
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}
}
