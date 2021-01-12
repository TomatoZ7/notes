package com.cbh.domain;

public class User {
	private int id;
	private String code;
	private String name;
	private int gender;
	private String phone;
	private int status;
	private int auth;
	private String id_num;
	private String apply_time;
	private String id_positive_img;
	private String id_negative_img;
	private int apply_status;
	
	public User(int id, String code, String name, int gender, String phone, int status, int auth, String id_num,
			String apply_time, String id_positive_img, String id_negative_img, int apply_status) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.gender = gender;
		this.phone = phone;
		this.status = status;
		this.auth = auth;
		this.id_num = id_num;
		this.apply_time = apply_time;
		this.id_positive_img = id_positive_img;
		this.id_negative_img = id_negative_img;
		this.apply_status = apply_status;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAuth() {
		return auth;
	}

	public void setAuth(int auth) {
		this.auth = auth;
	}

	public String getId_num() {
		return id_num;
	}

	public void setId_num(String id_num) {
		this.id_num = id_num;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getId_positive_img() {
		return id_positive_img;
	}

	public void setId_positive_img(String id_positive_img) {
		this.id_positive_img = id_positive_img;
	}

	public String getId_negative_img() {
		return id_negative_img;
	}

	public void setId_negative_img(String id_negative_img) {
		this.id_negative_img = id_negative_img;
	}

	public int getApply_status() {
		return apply_status;
	}

	public void setApply_status(int apply_status) {
		this.apply_status = apply_status;
	}
}
