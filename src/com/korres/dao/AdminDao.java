package com.korres.dao;

import com.korres.entity.Admin;

public abstract interface AdminDao extends BaseDao<Admin, Long> {
	public abstract boolean usernameExists(String paramString);

	public abstract Admin findByUsername(String paramString);
}