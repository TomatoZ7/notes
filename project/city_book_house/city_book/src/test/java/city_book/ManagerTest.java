package city_book;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cbh.dao.ManagerDao;
import com.cbh.domain.Manager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class ManagerTest {
	@Autowired
	ManagerDao managerDao;
	
	@Test
	public void testQueryManagerList() {
		Map<String,Object> param = new HashMap<>();
		param.put("offset", 0);
		param.put("limit", 10);
		param.put("status", 1);
		param.put("content", "admin");
		
		List<List<?>> list = managerDao.getManagerList(param);
		System.out.println(list);
	}
	
	@Test
    public void testQueryManager(){
        Manager Manager = managerDao.getManagerById(1);
        System.out.println(Manager);
    }
	
	@Test
	public void testInsertManager() {
		Manager Manager = new Manager();
		Manager.setName("testAdd0106");
		Manager.setPassword("testpwd");
        Manager.setAccount("testAdd0106");
		Manager.setGender(1);
		Manager.setPhone("13515013510");
		Manager.setAvatar("123456789");
		Manager.setStatus(1);
		Manager.setHas_rights(2);
		Manager.setCreate_time("2021-01-06 12:06:06");
        int result = managerDao.insertManager(Manager);
        System.out.println(result);
	}
	
	@Test
	public void testUpdateManager() {
		Manager Manager = managerDao.getManagerById(9);
		Manager.setName("ironman");
		Manager.setPassword("123456");
        Manager.setAccount("ironman666");
		Manager.setGender(1);
		Manager.setPhone("13515013510");
		Manager.setAvatar("1234567890");
//		Manager.setStatus(1);
//		Manager.setHas_rights(2);
        int result = managerDao.updateManager(Manager);
        System.out.println(result);
	}
	
	@Test
	public void testDelManager() {
		int result = managerDao.delManagerById(3);
        System.out.println(result);
	}
}
