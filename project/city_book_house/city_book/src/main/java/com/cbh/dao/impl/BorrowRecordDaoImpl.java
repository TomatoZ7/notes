package com.cbh.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbh.dao.BorrowRecordDao;

@Component
public class BorrowRecordDaoImpl implements BorrowRecordDao {
	@Autowired
    SqlSessionTemplate ssTemplate;

	@Override
	public List<List<?>> getBorrowRecordList(Map param) {
		// TODO Auto-generated method stub
		return ssTemplate.getMapper(BorrowRecordDao.class).getBorrowRecordList(param);
	}
}
