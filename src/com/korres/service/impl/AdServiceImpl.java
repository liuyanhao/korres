package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.AdDao;
import com.korres.entity.Ad;
import com.korres.service.AdService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("adServiceImpl")
public class AdServiceImpl extends BaseServiceImpl<Ad, Long> implements
		AdService {
	@Resource(name = "adDaoImpl")
	public void setBaseDao(AdDao adDao) {
		super.setBaseDao(adDao);
	}

	@Transactional
	@CacheEvict(value = { "adPosition" }, allEntries = true)
	public void save(Ad ad) {
		super.save(ad);
	}

	@Transactional
	@CacheEvict(value = { "adPosition" }, allEntries = true)
	public Ad update(Ad ad) {
		return super.update(ad);
	}

	@Transactional
	@CacheEvict(value = { "adPosition" }, allEntries = true)
	public Ad update(Ad ad, String[] ignoreProperties) {
		return super.update(ad, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "adPosition" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "adPosition" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "adPosition" }, allEntries = true)
	public void delete(Ad ad) {
		super.delete(ad);
	}
}