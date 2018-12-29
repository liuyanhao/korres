package com.korres.service;

import java.util.List;
import com.korres.entity.Admin;

public abstract interface AdminService extends BaseService<Admin, Long> {
	public abstract boolean usernameExists(String paramString);

	public abstract Admin findByUsername(String paramString);

	public abstract List<String> findAuthorities(Long paramLong);

	public abstract boolean isAuthenticated();

	public abstract Admin getCurrent();

	public abstract String getCurrentUsername();
}