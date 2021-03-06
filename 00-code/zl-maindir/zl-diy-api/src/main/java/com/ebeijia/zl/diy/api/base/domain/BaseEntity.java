package com.ebeijia.zl.diy.api.base.domain;

import java.io.Serializable;

public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private String createUser;
	private long createTime;
	private String updateUser;
	private long updateTime;
	private String remarks;
	private int lockVersion;
	private String dataStat;
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getLockVersion() {
		return lockVersion;
	}
	public void setLockVersion(int lockVersion) {
		this.lockVersion = lockVersion;
	}
	public String getDataStat() {
		return dataStat;
	}
	public void setDataStat(String dataStat) {
		this.dataStat = dataStat;
	}

}
