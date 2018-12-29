package com.korres.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.korres.dao.ProductDao;
import com.korres.dao.ReviewDao;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Review;
import com.korres.entity.Review.ReviewType;
import com.korres.service.ReviewService;
import com.korres.service.StaticService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

@Service("reviewServiceImpl")
public class ReviewServiceImpl extends BaseServiceImpl<Review, Long> implements
		ReviewService {

	@Resource(name = "reviewDaoImpl")
	private ReviewDao reviewDao;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "reviewDaoImpl")
	public void setBaseDao(ReviewDao reviewDao) {
		super.setBaseDao(reviewDao);
	}

	@Transactional(readOnly = true)
	public List<Review> findList(Member member, Product product,
			ReviewType type, Boolean isShow, Integer count,
			List<Filter> filters, List<Order> orders) {
		return this.reviewDao.findList(member, product, type, isShow, count,
				filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "review" })
	public List<Review> findList(Member member, Product product,
			ReviewType type, Boolean isShow, Integer count,
			List<Filter> filters, List<Order> orders, String cacheRegion) {
		return this.reviewDao.findList(member, product, type, isShow, count,
				filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<Review> findPage(Member member, Product product,
			ReviewType type, Boolean isShow, Pageable pageable) {
		return this.reviewDao.findPage(member, product, type, isShow, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Member member, Product product, ReviewType type,
			Boolean isShow) {
		return this.reviewDao.count(member, product, type, isShow);
	}

	@Transactional(readOnly = true)
	public boolean isReviewed(Member member, Product product) {
		return this.reviewDao.isReviewed(member, product);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void save(Review review) {
		super.save(review);
		Product product = review.getProduct();
		if (product != null) {
			this.reviewDao.flush();
			long totalScore = this.reviewDao.calculateTotalScore(product);
			long scoreCount = this.reviewDao.calculateScoreCount(product);
			product.setTotalScore(Long.valueOf(totalScore));
			product.setScoreCount(Long.valueOf(scoreCount));
			this.productDao.merge(product);
			this.reviewDao.flush();
			this.staticService.build(product);
		}
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public Review update(Review review) {
		Review r = super.update(review);
		Product product = r.getProduct();
		if (product != null) {
			this.reviewDao.flush();
			long l1 = this.reviewDao.calculateTotalScore(product);
			long l2 = this.reviewDao.calculateScoreCount(product);
			product.setTotalScore(Long.valueOf(l1));
			product.setScoreCount(Long.valueOf(l2));
			this.productDao.merge(product);
			this.reviewDao.flush();
			this.staticService.build(product);
		}

		return r;
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public Review update(Review review, String[] ignoreProperties) {
		return super.update(review, ignoreProperties);
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
	public void delete(Review review) {
		if (review != null) {
			super.delete(review);
			Product product = review.getProduct();
			if (product != null) {
				this.reviewDao.flush();
				long totalScore = this.reviewDao.calculateTotalScore(product);
				long scoreCount = this.reviewDao.calculateScoreCount(product);
				product.setTotalScore(Long.valueOf(totalScore));
				product.setScoreCount(Long.valueOf(scoreCount));
				this.productDao.merge(product);
				this.reviewDao.flush();
				this.staticService.build(product);
			}
		}
	}
}