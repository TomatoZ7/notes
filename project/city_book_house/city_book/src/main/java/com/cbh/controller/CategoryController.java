package com.cbh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.service.CategoryService;

@RequestMapping(value = "/category")
@RestController
public class CategoryController {
	@Autowired
    CategoryService categoryService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(
			@RequestParam Integer offset,
			@RequestParam Integer limit) {
		Map<String,Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		
		List<List<?>> list = categoryService.getCategoryList(param);
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}
}
