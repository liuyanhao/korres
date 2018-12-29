package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.PromotionDao;
import com.korres.entity.Promotion;
import com.korres.service.PromotionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;

@Service("promotionServiceImpl")
public class PromotionServiceImpl extends BaseServiceImpl<Promotion, Long>
		implements PromotionService {

	@Resource(name = "promotionDaoImpl")
	private PromotionDao promotionDao;

	@Resource(name = "promotionDaoImpl")
	public void setBaseDao(PromotionDao promotionDao) {
		super.setBaseDao(promotionDao);
	}

	@Transactional(readOnly = true)
	public List<Promotion> findList(Boolean hasBegun, Boolean hasEnded,
			Integer count, List<Filter> filters, List<Order> orders) {
		return this.promotionDao.findList(hasBegun, hasEnded, count, filters,
				orders);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "promotion" })
	public List<Promotion> findList(Boolean hasBegun, Boolean hasEnded,
			Integer count, List<Filter> filters, List<Order> orders,
			String cacheRegion) {
		return this.promotionDao.findList(hasBegun, hasEnded, count, filters,
				orders);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void save(Promotion promotion) {
		super.save(promotion);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public Promotion update(Promotion promotion) {
		return super.update(promotion);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public Promotion update(Promotion promotion, String[] ignoreProperties) {
		return super.update(promotion, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Promotion promotion) {
		super.delete(promotion);
	}
}