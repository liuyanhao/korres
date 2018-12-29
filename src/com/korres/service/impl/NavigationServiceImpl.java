package com.korres.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.korres.dao.NavigationDao;
import com.korres.entity.Navigation;
import com.korres.entity.Navigation.NavigationPosition;
import com.korres.service.NavigationService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;

@Service("navigationServiceImpl")
public class NavigationServiceImpl extends BaseServiceImpl<Navigation, Long>
		implements NavigationService {

	@Resource(name = "navigationDaoImpl")
	private NavigationDao navigationDao;

	@Resource(name = "navigationDaoImpl")
	public void setBaseDao(NavigationDao navigationDao) {
		super.setBaseDao(navigationDao);
	}

	@Transactional(readOnly = true)
	public List<Navigation> findList(NavigationPosition position) {
		return this.navigationDao.findList(position);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "navigation" })
	public List<Navigation> findList(Integer count, List<Filter> filters,
			List<Order> orders, String cacheRegion) {
		return this.navigationDao.findList(null, count, filters, orders);
	}

	@Transactional
	@CacheEvict(value = { "navigation" }, allEntries = true)
	public void save(Navigation navigation) {
		super.save(navigation);
	}

	@Transactional
	@CacheEvict(value = { "navigation" }, allEntries = true)
	public Navigation update(Navigation navigation) {
		return super.update(navigation);
	}

	@Transactional
	@CacheEvict(value = { "navigation" }, allEntries = true)
	public Navigation update(Navigation navigation, String[] ignoreProperties) {
		return super.update(navigation, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "navigation" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "navigation" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "navigation" }, allEntries = true)
	public void delete(Navigation navigation) {
		super.delete(navigation);
	}
}