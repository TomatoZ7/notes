package com.cbh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.domain.User;
import com.cbh.service.UserService;
import com.cbh.utils.Result;

@RequestMapping(value = "/user")
@RestController
public class UserController {
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(Integer offset, Integer limit, Integer apply_status, String name, String phone, Integer status, Integer auth) {
		
		Map<String,Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("apply_status", apply_status);
		param.put("name", name);
		param.put("phone", phone);
		param.put("status", status);
		param.put("auth", auth);
		
		List<List<?>> list = userService.getUserList(param);
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}
	
	// TODO ½èÔÄ¼ÇÂ¼
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User getUserByid(@PathVariable int id) {
		User user = userService.getUserById(id);
        if (user == null) {
        	return new User();
        }
        return user;
    }
	
	@RequestMapping(value = "/verify", method = RequestMethod.PUT)
	public Result verifyUser(@RequestBody Map<String, Object> params) {
//		System.out.println(params.get("id"));
//		System.out.println(params.get("apply_status").getClass().toString());
//		Map<String,Object> param = new HashMap<>();
		
		
		int result = userService.verifyUser(params);
//		int result = 1;
		if (result == 0) {
        	return new Result(403, "Ê§°Ü¡£");
        }

        return new Result(200, "³É¹¦¡£");
	}
}
