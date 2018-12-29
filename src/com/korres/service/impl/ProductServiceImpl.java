package com.korres.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.LockModeType;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import com.korres.dao.ProductDao;
import com.korres.entity.Attribute;
import com.korres.entity.Brand;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Product.ProductOrderType;
import com.korres.entity.ProductCategory;
import com.korres.entity.Promotion;
import com.korres.entity.Tag;
import com.korres.service.ProductService;
import com.korres.service.StaticService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

@Service("productServiceImpl")
public class ProductServiceImpl extends BaseServiceImpl<Product, Long>
		implements ProductService, DisposableBean {
	private long startTime = System.currentTimeMillis();

	@Resource(name = "ehCacheManager")
	private CacheManager cacheManager;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "productDaoImpl")
	public void setBaseDao(ProductDao productDao) {
		super.setBaseDao(productDao);
	}

	@Transactional(readOnly = true)
	public boolean snExists(String sn) {
		return this.productDao.snExists(sn);
	}

	@Transactional(readOnly = true)
	public Product findBySn(String sn) {
		return this.productDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public boolean snUnique(String previousSn, String currentSn) {
		if (StringUtils.equalsIgnoreCase(previousSn, currentSn))
			return true;
		return !this.productDao.snExists(currentSn);
	}

	@Transactional(readOnly = true)
	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		return this.productDao.search(keyword, isGift, count);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, ProductOrderType orderType, Integer count,
			List<Filter> filters, List<Order> orders) {
		return this.productDao.findList(productCategory, brand, promotion,
				tags, attributeValue, startPrice, endPrice, isMarketable,
				isList, isTop, isGift, isOutOfStock, isStockAlert, orderType,
				count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "product" })
	public List<Product> findList(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, ProductOrderType orderType, Integer count,
			List<Filter> filters, List<Order> orders, String cacheRegion) {
		return this.productDao.findList(productCategory, brand, promotion,
				tags, attributeValue, startPrice, endPrice, isMarketable,
				isList, isTop, isGift, isOutOfStock, isStockAlert, orderType,
				count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(ProductCategory productCategory,
			Date beginDate, Date endDate, Integer first, Integer count) {
		return this.productDao.findList(productCategory, beginDate, endDate,
				first, count);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, ProductOrderType orderType, Pageable pageable) {
		return this.productDao.findPage(productCategory, brand, promotion,
				tags, attributeValue, startPrice, endPrice, isMarketable,
				isList, isTop, isGift, isOutOfStock, isStockAlert, orderType,
				pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Member member, Pageable pageable) {
		return this.productDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Object> findSalesPage(Date beginDate, Date endDate,
			Pageable pageable) {
		return this.productDao.findSalesPage(beginDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Member favoriteMember, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift,
			Boolean isOutOfStock, Boolean isStockAlert) {
		return this.productDao.count(favoriteMember, isMarketable, isList,
				isTop, isGift, isOutOfStock, isStockAlert);
	}

	@Transactional(readOnly = true)
	public boolean isPurchased(Member member, Product product) {
		return this.productDao.isPurchased(member, product);
	}

	public long viewHits(Long id) {
		Ehcache ehcache = this.cacheManager.getEhcache("productHits");
		Element element = ehcache.get(id);
		Long hits = 0L;
		if (element != null) {
			hits = (Long) element.getObjectValue();
		} else {
			Product product = this.productDao.find(id);
			if (product == null) {
				return 0L;
			}

			hits = product.getHits();
		}

		Long viewHits = Long.valueOf(hits.longValue() + 1L);
		ehcache.put(new Element(id, viewHits));
		long endTiem = System.currentTimeMillis();
		if (endTiem > this.startTime + 600000L) {
			this.startTime = endTiem;
			removeProduct();
			ehcache.removeAll();
		}

		return viewHits.longValue();
	}

	public void destroy() {
		removeProduct();
	}

	private void removeProduct() {
		Ehcache ehcache = this.cacheManager.getEhcache("productHits");
		List<Long> localList = ehcache.getKeys();
		Iterator<Long> iterator = localList.iterator();
		while (iterator.hasNext()) {
			Long localLong = iterator.next();
			Product product = this.productDao.find(localLong);
			if (product != null) {
				this.productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				Element element = ehcache.get(localLong);
				long hits = ((Long) element.getObjectValue()).longValue();
				long l2 = hits - product.getHits().longValue();
				Calendar calendar = Calendar.getInstance();
				Calendar weekHitsDate = DateUtils.toCalendar(product
						.getWeekHitsDate());
				Calendar monthHitsDate = DateUtils.toCalendar(product
						.getMonthHitsDate());

				if ((calendar.get(1) != weekHitsDate.get(1))
						|| (calendar.get(3) > weekHitsDate.get(3))) {
					product.setWeekHits(Long.valueOf(l2));
				} else {
					product.setWeekHits(Long.valueOf(product.getWeekHits()
							.longValue()
							+ l2));
				}

				if ((calendar.get(1) != monthHitsDate.get(1))
						|| (calendar.get(2) > monthHitsDate.get(2))) {
					product.setMonthHits(Long.valueOf(l2));
				} else {
					product.setMonthHits(Long.valueOf(product.getMonthHits()
							.longValue()
							+ l2));
				}

				product.setHits(Long.valueOf(hits));
				product.setWeekHitsDate(new Date());
				product.setMonthHitsDate(new Date());

				this.productDao.merge(product);
			}
		}
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void save(Product product) {
		Assert.notNull(product);
		super.save(product);
		this.productDao.flush();
		this.staticService.build(product);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public Product update(Product product) {
		Assert.notNull(product);
		Product p = super.update(product);
		this.productDao.flush();
		this.staticService.build(p);

		return p;
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public Product update(Product product, String[] ignoreProperties) {
		return super.update(product, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void delete(Product product) {
		if (product != null) {
			this.staticService.delete(product);
		}

		super.delete(product);
	}
}
