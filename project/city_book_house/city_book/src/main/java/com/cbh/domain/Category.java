package com.cbh.domain;

public class Category {
	private int id;
	private String category_name;
	private int pid;
	private String create_time;
	private Category firstCate;
	
	/**
	 * @param id
	 * @param category_name
	 * @param pid
	 * @param create_time
	 */
	public Category(int id, String category_name, int pid, String create_time) {
		super();
		this.id = id;
		this.category_name = category_name;
		this.pid = pid;
		this.create_time = create_time;
	}

	public Category() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCategory_name() {
		return category_name;
	}
	
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	
	public int getPid() {
		return pid;
	}
	
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public String getCreate_time() {
		return create_time;
	}
	
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public Category getFirstCate() {
		return firstCate;
	}

	public void setFirstCate(Category firstCate) {
		this.firstCate = firstCate;
	}

	
}
