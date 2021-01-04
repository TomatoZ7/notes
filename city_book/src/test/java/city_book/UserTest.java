package city_book;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cbh.dao.UserDao;
import com.cbh.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class UserTest {
	@Autowired
	UserDao userDao;
	
	@Test
    public void testQueryUser(){
        User user = userDao.getUserById(1);
        System.out.println(user);
    }
	
	@Test
	public void testDelUser() {
		int result = userDao.delUserById(2);
        System.out.println(result);
	}
	
	@Test
	public void testUpdateUser() {
		Map<String,String> param = new HashMap<>();
        param.put("id","1");
        param.put("name", "admin-test");
        int result = userDao.updateUser(param);
        System.out.println(result);
	}
	
	@Test
	public void testInsertUser() {
		User user = new User();
		user.setName("manager");
        user.setAccount("manager888");
		user.setGender(0);
		user.setPassword("asd123asd");
        int result = userDao.insertUser(user);
        System.out.println(result);
	}
}
