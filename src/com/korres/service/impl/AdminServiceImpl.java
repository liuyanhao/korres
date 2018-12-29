package com.korres.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import com.korres.dao.AdminDao;
import com.korres.entity.Admin;
import com.korres.entity.Role;
import com.korres.service.AdminService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Principal;

@Service("adminServiceImpl")
public class AdminServiceImpl extends BaseServiceImpl<Admin, Long> implements
		AdminService {

	@Resource(name = "adminDaoImpl")
	private AdminDao adminDao;

	@Resource(name = "adminDaoImpl")
	public void setBaseDao(AdminDao adminDao) {
		super.setBaseDao(adminDao);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return this.adminDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public Admin findByUsername(String username) {
		return this.adminDao.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<String> findAuthorities(Long id) {
		List<String> list = new ArrayList<String>();
		Admin admin = this.adminDao.find(id);
		if (admin != null) {
			Iterator<Role> iterator = admin.getRoles().iterator();
			while (iterator.hasNext()) {
				Role role = iterator.next();
				list.addAll(role.getAuthorities());
			}
		}
		return list;
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null)
			return subject.isAuthenticated();
		return false;
	}

	@Transactional(readOnly = true)
	public Admin getCurrent() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null) {
				return this.adminDao.find(principal.getId());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public void save(Admin admin) {
		super.save(admin);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public Admin update(Admin admin) {
		return super.update(admin);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public Admin update(Admin admin, String[] ignoreProperties) {
		return super.update(admin, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public void delete(Admin admin) {
		super.delete(admin);
	}
}