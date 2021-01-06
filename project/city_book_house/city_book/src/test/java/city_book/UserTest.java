package city_book;
import java.util.HashMap;
import java.util.List;
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
	public void testQueryUserList() {
		Map<String,Integer> param = new HashMap<>();
		param.put("offset", 0);
		param.put("limit", 10);
		
		List<List<?>> list = userDao.getUserList(param);
		System.out.println(list.get(0));
		System.out.println(list.get(1));
	}
	
	@Test
    public void testQueryUser(){
        User user = userDao.getUserById(1);
        System.out.println(user);
    }
	
	@Test
	public void testInsertUser() {
		User user = new User();
		user.setName("testAdd0106");
		user.setPassword("testpwd");
        user.setAccount("testAdd0106");
		user.setGender(1);
		user.setPhone("13515013510");
		user.setAvatar("123456789");
		user.setStatus(1);
		user.setHas_rights(2);
		user.setCreate_time("2021-01-06 12:06:06");
        int result = userDao.insertUser(user);
        System.out.println(result);
	}
	
	@Test
	public void testUpdateUser() {
		User user = userDao.getUserById(9);
		user.setName("ironman");
		user.setPassword("123456");
        user.setAccount("ironman666");
		user.setGender(1);
		user.setPhone("13515013510");
		user.setAvatar("1234567890");
//		user.setStatus(1);
//		user.setHas_rights(2);
        int result = userDao.updateUser(user);
        System.out.println(result);
	}
	
	@Test
	public void testDelUser() {
		int result = userDao.delUserById(3);
        System.out.println(result);
	}
}
