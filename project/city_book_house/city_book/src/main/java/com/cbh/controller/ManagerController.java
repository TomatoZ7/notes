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

import com.cbh.domain.Manager;
import com.cbh.service.ManagerService;
import com.cbh.utils.Result;

@RequestMapping(value = "/manager")
@RestController
public class ManagerController {
	@Autowired
    ManagerService managerService;

	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(
			@RequestParam Integer offset, 
			@RequestParam Integer limit, 
			@RequestParam Integer status,
			@RequestParam String content) {
		Map<String,Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("status", status);
		param.put("content", content);
		
		List<List<?>> list = managerService.getManagerList(param);
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Manager getManagerById(@PathVariable int id) {
        Manager Manager = managerService.getManagerById(id);
        if (Manager == null) {
        	return new Manager();
        }
        return Manager;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addManager(Manager Manager) {
    	// 初始密码123456
    	String initPwd = DigestUtils.md5DigestAsHex("123456".getBytes());
    	Manager.setPassword(initPwd);
    	
    	// 设置创建时间
    	Manager.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    	
        int result = managerService.insertManager(Manager);
        if (result == 0) {
        	return new Result(403, "添加失败。");
        }

        return new Result(200, "添加成功。");
    }
    
    @RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public Result updateManager(@PathVariable int id, @RequestBody Manager paramManager){
    	Manager Manager = managerService.getManagerById(id);
    	Manager.setName(paramManager.getName());
    	Manager.setGender(paramManager.getGender());
    	Manager.setPhone(paramManager.getPhone());
    	
        int result = managerService.updateManager(Manager);
        if (result == 0) {
        	return new Result(403, "更新失败。");
        }

        return new Result(200, "更新成功。");
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delManagerById(@PathVariable int id) {
        int res = managerService.delManagerById(id);
        if (res == 0) {
        	return new Result(403, "删除失败");
        }

        return new Result(200, "删除操作成功");
    }
}
