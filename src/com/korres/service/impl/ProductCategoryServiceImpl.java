package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.ProductCategoryDao;
import com.korres.entity.ProductCategory;
import com.korres.service.ProductCategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("productCategoryServiceImpl")
public class ProductCategoryServiceImpl extends
		BaseServiceImpl<ProductCategory, Long> implements
		ProductCategoryService {

	@Resource(name = "productCategoryDaoImpl")
	private ProductCategoryDao productCategoryDao;

	@Resource(name = "productCategoryDaoImpl")
	public void setBaseDao(ProductCategoryDao productCategoryDao) {
		super.setBaseDao(productCategoryDao);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots() {
		return this.productCategoryDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots(Integer count) {
		return this.productCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "productCategory" })
	public List<ProductCategory> findRoots(Integer count, String cacheRegion) {
		return this.productCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParents(ProductCategory productCategory) {
		return this.productCategoryDao.findParents(productCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParents(ProductCategory productCategory,
			Integer count) {
		return this.productCategoryDao.findParents(productCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "productCategory" })
	public List<ProductCategory> findParents(ProductCategory productCategory,
			Integer count, String cacheRegion) {
		return this.productCategoryDao.findParents(productCategory, count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findTree() {
		return this.productCategoryDao.findChildren(null, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findChildren(ProductCategory productCategory) {
		return this.productCategoryDao.findChildren(productCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findChildren(ProductCategory productCategory,
			Integer count) {
		return this.productCategoryDao.findChildren(productCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "productCategory" })
	public List<ProductCategory> findChildren(ProductCategory productCategory,
			Integer count, String cacheRegion) {
		return this.productCategoryDao.findChildren(productCategory, count);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void save(ProductCategory productCategory) {
		super.save(productCategory);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public ProductCategory update(ProductCategory productCategory) {
		return super.update(productCategory);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public ProductCategory update(ProductCategory productCategory,
			String[] ignoreProperties) {
		return super.update(productCategory, ignoreProperties);
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
	public void delete(ProductCategory productCategory) {
		super.delete(productCategory);
	}
}