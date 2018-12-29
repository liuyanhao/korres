package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.SeoDao;
import com.korres.entity.Seo;
import com.korres.entity.Seo.SeoType;
import com.korres.service.SeoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("seoServiceImpl")
public class SeoServiceImpl extends BaseServiceImpl<Seo, Long> implements
		SeoService {

	@Resource(name = "seoDaoImpl")
	private SeoDao seoDao;

	@Resource(name = "seoDaoImpl")
	public void setBaseDao(SeoDao seoDao) {
		super.setBaseDao(seoDao);
	}

	@Transactional(readOnly = true)
	public Seo find(SeoType type) {
		return this.seoDao.find(type);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "seo" })
	public Seo find(SeoType type, String cacheRegion) {
		return this.seoDao.find(type);
	}

	@Transactional
	@CacheEvict(value = { "seo" }, allEntries = true)
	public void save(Seo seo) {
		super.save(seo);
	}

	@Transactional
	@CacheEvict(value = { "seo" }, allEntries = true)
	public Seo update(Seo seo) {
		return super.update(seo);
	}

	@Transactional
	@CacheEvict(value = { "seo" }, allEntries = true)
	public Seo update(Seo seo, String[] ignoreProperties) {
		return super.update(seo, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "seo" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "seo" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "seo" }, allEntries = true)
	public void delete(Seo seo) {
		super.delete(seo);
	}
}