package com.cbh.dao;

import java.util.List;
import java.util.Map;

public interface BookDao {
	List<List<?>> getBookList(Map param);
}
