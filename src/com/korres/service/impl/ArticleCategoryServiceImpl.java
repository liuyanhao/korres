package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.ArticleCategoryDao;
import com.korres.entity.ArticleCategory;
import com.korres.service.ArticleCategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("articleCategoryServiceImpl")
public class ArticleCategoryServiceImpl extends
		BaseServiceImpl<ArticleCategory, Long> implements
		ArticleCategoryService {

	@Resource(name = "articleCategoryDaoImpl")
	private ArticleCategoryDao articleCategoryDao;

	@Resource(name = "articleCategoryDaoImpl")
	public void setBaseDao(ArticleCategoryDao articleCategoryDao) {
		super.setBaseDao(articleCategoryDao);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findRoots() {
		return this.articleCategoryDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findRoots(Integer count) {
		return this.articleCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "articleCategory" })
	public List<ArticleCategory> findRoots(Integer count, String cacheRegion) {
		return this.articleCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findParents(ArticleCategory articleCategory) {
		return this.articleCategoryDao.findParents(articleCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findParents(ArticleCategory articleCategory,
			Integer count) {
		return this.articleCategoryDao.findParents(articleCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "articleCategory" })
	public List<ArticleCategory> findParents(ArticleCategory articleCategory,
			Integer count, String cacheRegion) {
		return this.articleCategoryDao.findParents(articleCategory, count);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findTree() {
		return this.articleCategoryDao.findChildren(null, null);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory) {
		return this.articleCategoryDao.findChildren(articleCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory,
			Integer count) {
		return this.articleCategoryDao.findChildren(articleCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "articleCategory" })
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory,
			Integer count, String cacheRegion) {
		return this.articleCategoryDao.findChildren(articleCategory, count);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void save(ArticleCategory articleCategory) {
		super.save(articleCategory);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public ArticleCategory update(ArticleCategory articleCategory) {
		return super.update(articleCategory);
	}

	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public ArticleCategory update(ArticleCategory articleCategory,
			String[] ignoreProperties) {
		return super.update(articleCategory, ignoreProperties);
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
	public void delete(ArticleCategory articleCategory) {
		super.delete(articleCategory);
	}
}