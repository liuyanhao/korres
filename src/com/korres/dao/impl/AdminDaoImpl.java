package com.korres.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import com.korres.dao.AdminDao;
import com.korres.entity.Admin;

import org.springframework.stereotype.Repository;

@Repository("adminDaoImpl")
public class AdminDaoImpl extends BaseDaoImpl<Admin, Long> implements AdminDao {
	public boolean usernameExists(String username) {
		if (username == null)
			return false;
		String str = "select count(*) from Admin admin where lower(admin.username) = lower(:username)";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("username",
						username).getSingleResult();
		return localLong.longValue() > 0L;
	}

	public Admin findByUsername(String username) {
		if (username == null)
			return null;
		try {
			String str = "select admin from Admin admin where lower(admin.username) = lower(:username)";
			return (Admin) this.entityManager.createQuery(str, Admin.class)
					.setFlushMode(FlushModeType.COMMIT).setParameter(
							"username", username).getSingleResult();
		} catch (NoResultException localNoResultException1) {
		}
		return null;
	}
}