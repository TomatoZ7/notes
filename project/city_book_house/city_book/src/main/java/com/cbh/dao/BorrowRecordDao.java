package com.cbh.dao;

import java.util.List;
import java.util.Map;

public interface BorrowRecordDao {
	List<List<?>> getBorrowRecordList(Map param);
}
