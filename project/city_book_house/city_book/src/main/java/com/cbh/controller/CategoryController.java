package com.cbh.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cbh.domain.Category;
import com.cbh.domain.Manager;
import com.cbh.service.CategoryService;
import com.cbh.utils.Result;

@RequestMapping(value = "/category")
@RestController
public class CategoryController {
	@Autowired
    CategoryService categoryService;
	
	// TODO 条件搜索
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
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Category getCategoryByid(@PathVariable int id) {
		Category category = categoryService.getCategoryByid(id);
        if (category == null) {
        	return new Category();
        }
        return category;
    }
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result insertCategory(String first_cate, String second_cate) {
		int result = 1;
		
		// 一级分类处理
		Category firstCate = categoryService.getCategoryByName(first_cate);
		System.out.println(firstCate);
		int pid;

		if (firstCate == null) {
			Category newFirstCate = new Category();
			newFirstCate.setCategory_name(first_cate);
			newFirstCate.setPid(0);
			newFirstCate.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			categoryService.insertCategory(newFirstCate);
			
			pid = newFirstCate.getId();
		}else {
			pid = firstCate.getId();
		}
		
		// 二级分类处理
		if (second_cate != null) {
			Category secondCate = new Category();
			secondCate.setCategory_name(second_cate);
			secondCate.setPid(pid);
			secondCate.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			result = categoryService.insertCategory(secondCate);
		}
		
		if (result == 0) {
        	return new Result(403, "失败。");
        }

        return new Result(200, "成功。");
	}
	
	@RequestMapping(value="/{id}",method = RequestMethod.PUT)
	public Result updateCategory(@PathVariable int id, @RequestBody Map<String, String> params) {
		String first_cate = params.get("first_cate");
		String second_cate = params.get("second_cate");
		
		// 二级分类
		Category secondCate = categoryService.getCategoryByid(id);
		// 一级分类
		Category firstCate = secondCate.getFirstCate();
		
		if (firstCate.getCategory_name() != first_cate) {
			// 一级分类 修改
			firstCate.setCategory_name(first_cate);
			categoryService.updateCategory(firstCate);
		}

		secondCate.setCategory_name(second_cate);
		int result = categoryService.updateCategory(secondCate);
		
        if (result == 0) {
        	return new Result(403, "更新失败。");
        }

        return new Result(200, "更新成功。");
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delCategoryById(@PathVariable int id) {
        int res = categoryService.delCategoryById(id);
        if (res == 0) {
        	return new Result(403, "删除失败");
        }

        return new Result(200, "删除成功");
    }
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public HashMap<?,?> all() {
		List<List<?>> list = categoryService.getCategoryWithLevel();
		
		HashMap<String,Object> res = new HashMap<>();
		res.put("first", list.get(0));
		res.put("second", list.get(1));
		return res;
	}
	
	// TODO 批量导入、导入模板
}
