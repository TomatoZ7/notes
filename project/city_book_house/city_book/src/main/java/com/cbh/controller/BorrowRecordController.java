package com.cbh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.service.BorrowRecordService;

@RequestMapping(value = "/borrow_record")
@RestController
public class BorrowRecordController {
	@Autowired
	BorrowRecordService borrowRecordService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(@RequestParam Integer offset, @RequestParam Integer limit,
								Integer status, String start_time, String end_time, String content) {
		
		Map<String,Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("status", status);
		param.put("start_time", start_time);
		param.put("end_time", end_time);
		param.put("content", content);
		
		List<List<?>> list = borrowRecordService.getBorrowRecordList(param);
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}
}
