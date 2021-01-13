package com.cbh.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbh.dao.BorrowRecordDao;
import com.cbh.service.BorrowRecordService;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
	@Autowired
	BorrowRecordDao borrowRecordDao;
	
	@Override
	public List<List<?>> getBorrowRecordList(Map param) {
		return borrowRecordDao.getBorrowRecordList(param);
	}
}
