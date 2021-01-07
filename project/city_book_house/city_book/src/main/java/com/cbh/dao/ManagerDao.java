package com.cbh.dao;

import java.util.List;
import java.util.Map;

import com.cbh.domain.Manager;

public interface ManagerDao {
	// 对于其参数名与mapper中不一致情况，可适应@Param("名称") 注解进行校正
	List<List<?>> getManagerList(Map param);
	
    Manager getManagerById(int id);
    
    int delManagerById(int id);
    
    int updateManager(Manager Manager);

    int insertManager(Manager Manager);
}
