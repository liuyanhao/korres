package com.korres.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.korres.dao.ReviewDao;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Review;
import com.korres.entity.Review.ReviewType;

import org.springframework.stereotype.Repository;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

@Repository("reviewDaoImpl")
public class ReviewDaoImpl extends BaseDaoImpl<Review, Long> implements
		ReviewDao {
	public List<Review> findList(Member member, Product product,
			ReviewType type, Boolean isShow, Integer count,
			List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Review.class);
		Root root = criteriaQuery.from(Review.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		if (member != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("member"), member));
		}

		if (product != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("product"), product));
		}

		if (type == ReviewType.positive) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.ge(root
					.get("score"), Integer.valueOf(4)));
		} else if (type == ReviewType.moderate) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("score"), Integer.valueOf(3)));
		} else if (type == ReviewType.negative) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(root
					.get("score"), Integer.valueOf(2)));
		}

		if (isShow != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("isShow"), isShow));
		}

		criteriaQuery.where(predicate);

		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public Page<Review> findPage(Member member, Product product,
			ReviewType type, Boolean isShow, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Review.class);
		Root root = criteriaQuery.from(Review.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		if (member != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("member"), member));
		}

		if (product != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("product"), product));
		}

		if (type == ReviewType.positive) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.ge(root
					.get("score"), Integer.valueOf(4)));
		}

		else if (type == ReviewType.moderate) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("score"), Integer.valueOf(3)));
		} else if (type == ReviewType.negative) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(root
					.get("score"), Integer.valueOf(2)));
		}

		if (isShow != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("isShow"), isShow));
		}

		criteriaQuery.where(predicate);

		return super.findPage(criteriaQuery, pageable);
	}

	public Long count(Member member, Product product, ReviewType type,
			Boolean isShow) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Review.class);
		Root root = criteriaQuery.from(Review.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		if (member != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("member"), member));
		}

		if (product != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("product"), product));
		}

		if (type == ReviewType.positive) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.ge(root
					.get("score"), Integer.valueOf(4)));
		} else if (type == ReviewType.moderate) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("score"), Integer.valueOf(3)));
		} else if (type == ReviewType.negative) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(root
					.get("score"), Integer.valueOf(2)));
		}

		if (isShow != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("isShow"), isShow));
		}

		criteriaQuery.where(predicate);

		return super.count(criteriaQuery, null);
	}

	public boolean isReviewed(Member member, Product product) {
		if ((member == null) || (product == null)) {
			return false;
		}

		String sql = "select count(*) from Review review where review.member = :member and review.product = :product";
		Long count = (Long) this.entityManager.createQuery(sql, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("member",
						member).setParameter("product", product)
				.getSingleResult();
		return count.longValue() > 0L;
	}

	public long calculateTotalScore(Product product) {
		if (product == null) {
			return 0L;
		}

		String sql = "select sum(review.score) from Review review where review.product = :product and review.isShow = :isShow";
		Long sum = (Long) this.entityManager.createQuery(sql, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("product",
						product).setParameter("isShow", Boolean.valueOf(true))
				.getSingleResult();
		return sum != null ? sum.longValue() : 0L;
	}

	public long calculateScoreCount(Product product) {
		if (product == null) {
			return 0L;
		}

		String sql = "select count(*) from Review review where review.product = :product and review.isShow = :isShow";
		return ((Long) this.entityManager.createQuery(sql, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("product",
						product).setParameter("isShow", Boolean.valueOf(true))
				.getSingleResult()).longValue();
	}
}