package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.RoleDao;
import com.korres.entity.Role;
import com.korres.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("roleServiceImpl")
public class RoleServiceImpl extends BaseServiceImpl<Role, Long> implements
		RoleService {
	@Resource(name = "roleDaoImpl")
	public void setBaseDao(RoleDao roleDao) {
		super.setBaseDao(roleDao);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public void save(Role role) {
		super.save(role);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public Role update(Role role) {
		return super.update(role);
	}

	@Transactional
	@CacheEvict(value = { "authorization" }, allEntries = true)
	public Role update(Role role, String[] ignoreProperties) {
		return super.update(role, ignoreProperties);
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
	public void delete(Role role) {
		super.delete(role);
	}
}