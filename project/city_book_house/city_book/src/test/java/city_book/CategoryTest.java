package city_book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cbh.dao.CategoryDao;
import com.cbh.domain.Category;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class CategoryTest {
	@Autowired
	CategoryDao categoryDao;
	
	@Test
	public void testQueryCategoryList() {
		Map<String,Object> param = new HashMap<>();
		param.put("offset", 0);
		param.put("limit", 10);
		
		List<List<?>> list = categoryDao.getCategoryList(param);
		System.out.println(list);
	}
	
	@Test
	public void testQueryCategory() {
		Category category = categoryDao.getCategoryById(1);
        System.out.println(category);
	}
}
