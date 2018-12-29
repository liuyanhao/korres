package com.korres.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import com.korres.dao.ArticleDao;
import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.entity.Tag;
import com.korres.service.ArticleService;
import com.korres.service.StaticService;
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

@Service("articleServiceImpl")
public class ArticleServiceImpl extends BaseServiceImpl<Article, Long>
		implements ArticleService, DisposableBean {
	private long startTime = System.currentTimeMillis();

	@Resource(name = "ehCacheManager")
	private CacheManager cacheManager;

	@Resource(name = "articleDaoImpl")
	private ArticleDao articleDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "articleDaoImpl")
	public void setBaseDao(ArticleDao articleDao) {
		super.setBaseDao(articleDao);
	}

	@Transactional(readOnly = true)
	public List<Article> findList(ArticleCategory articleCategory,
			List<Tag> tags, Integer count, List<Filter> filters,
			List<Order> orders) {
		return this.articleDao.findList(articleCategory, tags, count, filters,
				orders);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "article" })
	public List<Article> findList(ArticleCategory articleCategory,
			List<Tag> tags, Integer count, List<Filter> filters,
			List<Order> orders, String cacheRegion) {
		return this.articleDao.findList(articleCategory, tags, count, filters,
				orders);
	}

	@Transactional(readOnly = true)
	public List<Article> findList(ArticleCategory articleCategory,
			Date beginDate, Date endDate, Integer first, Integer count) {
		return this.articleDao.findList(articleCategory, beginDate, endDate,
				first, count);
	}

	@Transactional(readOnly = true)
	public Page<Article> findPage(ArticleCategory articleCategory,
			List<Tag> tags, Pageable pageable) {
		return this.articleDao.findPage(articleCategory, tags, pageable);
	}

	public long viewHits(Long id) {
		Ehcache localEhcache = this.cacheManager.getEhcache("articleHits");
		Element localElement = localEhcache.get(id);
		Long localLong1;
		if (localElement != null) {
			localLong1 = (Long) localElement.getObjectValue();
		} else {
			Article localArticle = (Article) this.articleDao.find(id);
			if (localArticle == null)
				return 0L;
			localLong1 = localArticle.getHits();
		}
		Long localLong = Long.valueOf(localLong1.longValue() + 1L);
		localEhcache.put(new Element(id, localLong));
		long l = System.currentTimeMillis();
		if (l > this.startTime + 600000L) {
			this.startTime = l;
			IIIllIlI();
			localEhcache.removeAll();
		}
		return localLong.longValue();
	}

	public void destroy() {
		IIIllIlI();
	}

	private void IIIllIlI() {
		Ehcache localEhcache = this.cacheManager.getEhcache("articleHits");
		List localList = localEhcache.getKeys();
		Iterator localIterator = localList.iterator();
		while (localIterator.hasNext()) {
			Long localLong = (Long) localIterator.next();
			Article localArticle = (Article) this.articleDao.find(localLong);
			if (localArticle != null) {
				Element localElement = localEhcache.get(localLong);
				long l = ((Long) localElement.getObjectValue()).longValue();
				localArticle.setHits(Long.valueOf(l));
				this.articleDao.merge(localArticle);
			}
		}
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void save(Article article) {
		Assert.notNull(article);
		super.save(article);
		this.articleDao.flush();
		this.staticService.build(article);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public Article update(Article article) {
		Assert.notNull(article);
		Article localArticle = (Article) super.update(article);
		this.articleDao.flush();
		this.staticService.build(localArticle);
		return localArticle;
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public Article update(Article article, String[] ignoreProperties) {
		return super.update(article, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void delete(Article article) {
		if (article != null) {
			this.staticService.delete(article);
		}

		super.delete(article);
	}
}