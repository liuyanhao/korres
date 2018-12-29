package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.BrandDao;
import com.korres.entity.Brand;
import com.korres.service.BrandService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;

@Service("brandServiceImpl")
public class BrandServiceImpl extends BaseServiceImpl<Brand, Long> implements
		BrandService {

	@Resource(name = "brandDaoImpl")
	private BrandDao brandDao;

	@Resource(name = "brandDaoImpl")
	public void setBaseDao(BrandDao brandDao) {
		super.setBaseDao(brandDao);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "brand" })
	public List<Brand> findList(Integer count, List<Filter> filters,
			List<Order> orders, String cacheRegion) {
		return this.brandDao.findList(null, count, filters, orders);
	}

	@Transactional
	@CacheEvict(value = { "brand" }, allEntries = true)
	public void save(Brand brand) {
		super.save(brand);
	}

	@Transactional
	@CacheEvict(value = { "brand" }, allEntries = true)
	public Brand update(Brand brand) {
		return super.update(brand);
	}

	@Transactional
	@CacheEvict(value = { "brand" }, allEntries = true)
	public Brand update(Brand brand, String[] ignoreProperties) {
		return super.update(brand, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "brand" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "brand" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "brand" }, allEntries = true)
	public void delete(Brand brand) {
		super.delete(brand);
	}
}