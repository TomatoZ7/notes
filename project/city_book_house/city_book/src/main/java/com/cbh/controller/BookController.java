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
import org.springframework.web.bind.annotation.RestController;

import com.cbh.domain.Book;
import com.cbh.domain.Category;
import com.cbh.domain.Manager;
import com.cbh.service.BookService;
import com.cbh.utils.Result;

@RequestMapping(value = "/book")
@RestController
public class BookController {
	@Autowired
	BookService bookservice;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public HashMap<?,?> index(Integer offset, Integer limit, Integer first_category_id, Integer second_category_id, 
			String book_name, Integer status, String content) {

		Map<String,Object> param = new HashMap<>();
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("first_category_id", first_category_id);
		param.put("second_category_id", second_category_id);
		param.put("book_name", book_name);
		param.put("status", status);
		param.put("content", content);
		
		List<List<?>> list = bookservice.getBookList(param);
		HashMap<String,Object> res = new HashMap<>();
		res.put("data", list.get(0));
		res.put("total", list.get(1).get(0));
		return res;
	}
	
	// TODO 借阅记录
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Book getBookByid(@PathVariable int id) {
		Book book = bookservice.getBookById(id);
        if (book == null) {
        	return new Book();
        }
        return book;
    }
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result insertBook(Book book) {
		// 设置创建时间
    	book.setCreate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		
    	int result = bookservice.insertBook(book);
        if (result == 0) {
        	return new Result(403, "添加失败。");
        }

        return new Result(200, "添加成功。");
	}
	
	@RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public Result updateBook(@PathVariable int id, @RequestBody Book book){
		book.setId(id);
    	
        int result = bookservice.updateBook(book);
        if (result == 0) {
        	return new Result(403, "更新失败。");
        }

        return new Result(200, "更新成功。");
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delBookById(@PathVariable int id) {
        int res = bookservice.delBookById(id);
        if (res == 0) {
        	return new Result(403, "删除失败");
        }

        return new Result(200, "删除成功");
    }
	
	// TODO 批量导入
}
