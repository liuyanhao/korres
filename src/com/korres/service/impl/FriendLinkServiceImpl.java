package com.korres.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.korres.dao.FriendLinkDao;
import com.korres.entity.FriendLink;
import com.korres.entity.FriendLink.FriendLinkType;
import com.korres.service.FriendLinkService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;

@Service("friendLinkServiceImpl")
public class FriendLinkServiceImpl extends BaseServiceImpl<FriendLink, Long>
		implements FriendLinkService {

	@Resource(name = "friendLinkDaoImpl")
	public FriendLinkDao friendLinkDao;

	@Resource(name = "friendLinkDaoImpl")
	public void setBaseDao(FriendLinkDao friendLinkDao) {
		super.setBaseDao(friendLinkDao);
	}

	@Transactional(readOnly = true)
	public List<FriendLink> findList(FriendLinkType type) {
		return this.friendLinkDao.findList(type);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "friendLink" })
	public List<FriendLink> findList(Integer count, List<Filter> filters,
			List<Order> orders, String cacheRegion) {
		return this.friendLinkDao.findList(null, count, filters, orders);
	}

	@Transactional
	@CacheEvict(value = { "friendLink" }, allEntries = true)
	public void save(FriendLink friendLink) {
		super.save(friendLink);
	}

	@Transactional
	@CacheEvict(value = { "friendLink" }, allEntries = true)
	public FriendLink update(FriendLink friendLink) {
		return (FriendLink) super.update(friendLink);
	}

	@Transactional
	@CacheEvict(value = { "friendLink" }, allEntries = true)
	public FriendLink update(FriendLink friendLink, String[] ignoreProperties) {
		return (FriendLink) super.update(friendLink, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "friendLink" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "friendLink" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "friendLink" }, allEntries = true)
	public void delete(FriendLink friendLink) {
		super.delete(friendLink);
	}
}