package com.cbh.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.domain.User;
import com.cbh.pojo.Result;
import com.cbh.service.UserService;

@RequestMapping(value = "/user")
@RestController
public class UserController {
	@Autowired
    UserService userService;
	
	// TODO ��������
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(@RequestParam Integer offset, @RequestParam Integer limit) {
		Map<String,Integer> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		
		List<List<?>> list = userService.getUserList(param);
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}

	// TODO �����ѯ
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
        	return new User();
        }
        return user;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addUser(User user) {
    	// ��ʼ����123456
    	String initPwd = DigestUtils.md5DigestAsHex("123456".getBytes());
    	user.setPassword(initPwd);
    	
    	// ���ô���ʱ��
    	user.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    	
        int result = userService.insertUser(user);
        if (result == 0) {
        	return new Result(403, "���ʧ�ܡ�");
        }

        return new Result(200, "��ӳɹ���");
    }
    
    @RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public Result updateUser(@PathVariable int id, @RequestBody User paramUser){
    	User user = userService.getUserById(id);
    	user.setAccount(paramUser.getAccount());
    	user.setName(paramUser.getName());
    	user.setGender(paramUser.getGender());
    	user.setPhone(paramUser.getPhone());
    	
        int result = userService.updateUser(user);
        if (result == 0) {
        	return new Result(403, "����ʧ�ܡ�");
        }

        return new Result(200, "���³ɹ���");
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delUserById(@PathVariable int id) {
        int res = userService.delUserById(id);
        if (res == 0) {
        	return new Result(403, "ɾ��ʧ��");
        }

        return new Result(200, "ɾ�������ɹ�");
    }
}
