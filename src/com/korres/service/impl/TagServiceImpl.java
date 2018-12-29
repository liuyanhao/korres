package com.korres.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.korres.dao.TagDao;
import com.korres.entity.Tag;
import com.korres.entity.Tag.TagType;
import com.korres.service.TagService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;

@Service("tagServiceImpl")
public class TagServiceImpl extends BaseServiceImpl<Tag, Long> implements
		TagService {

	@Resource(name = "tagDaoImpl")
	private TagDao tagDao;

	@Resource(name = "tagDaoImpl")
	public void setBaseDao(TagDao tagDao) {
		super.setBaseDao(tagDao);
	}

	@Transactional(readOnly = true)
	public List<Tag> findList(TagType type) {
		return this.tagDao.findList(type);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "tag" })
	public List<Tag> findList(Integer count, List<Filter> filters,
			List<Order> orders, String cacheRegion) {
		return this.tagDao.findList(null, count, filters, orders);
	}

	@Transactional
	@CacheEvict(value = { "tag" }, allEntries = true)
	public void save(Tag tag) {
		super.save(tag);
	}

	@Transactional
	@CacheEvict(value = { "tag" }, allEntries = true)
	public Tag update(Tag tag) {
		return (Tag) super.update(tag);
	}

	@Transactional
	@CacheEvict(value = { "tag" }, allEntries = true)
	public Tag update(Tag tag, String[] ignoreProperties) {
		return (Tag) super.update(tag, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "tag" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "tag" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "tag" }, allEntries = true)
	public void delete(Tag tag) {
		super.delete(tag);
	}
}