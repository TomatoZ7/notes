package city_book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cbh.dao.BookDao;
import com.cbh.domain.Book;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class BookTest {
	@Autowired
	BookDao bookDao;
	
	@Test
	public void testQueryBookList() {
		Map<String,Object> param = new HashMap<>();
		param.put("offset", 0);
		param.put("limit", 10);
		
		List<List<?>> list = bookDao.getBookList(param);
		System.out.println(list);
	}
	
	@Test
	public void testQueryBookSingle() {
		Book book = bookDao.getBookById(2);
		System.out.println(book);
	}
}
