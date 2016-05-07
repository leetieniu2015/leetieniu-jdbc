package org.leetieniu.jdbc.model;

import java.util.Date;

public class User {
	
	private Integer id;
	private String userName;
	private String password;
	private Date createTime;
	private Double rmb;
	
	public Double getRmb() {
		return rmb;
	}
	public void setRmb(Double rmb) {
		this.rmb = rmb;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password="
				+ password + ", createTime=" + createTime + ", rmb=" + rmb
				+ "]";
	}
}
