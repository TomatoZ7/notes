package com.cbh.domain;

public class User {
	private int id;
	private String account;
	private String password;
	private String name;
	private int gender;
	private String phone;
	private String avatar;
	private int status;
	private int has_rights;
	private String create_time;
	
	public User(int id, String account, String password, String name, int gender, String phone, String avatar,
			int status, int has_rights, String create_time) {
		super();
		this.id = id;
		this.account = account;
		this.password = password;
		this.name = name;
		this.gender = gender;
		this.phone = phone;
		this.avatar = avatar;
		this.status = status;
		this.has_rights = has_rights;
		this.create_time = create_time;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getHas_rights() {
		return has_rights;
	}

	public void setHas_rights(int has_rights) {
		this.has_rights = has_rights;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
}
