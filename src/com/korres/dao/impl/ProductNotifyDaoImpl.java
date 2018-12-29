package com.korres.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.korres.dao.ProductNotifyDao;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.ProductNotify;
import org.springframework.stereotype.Repository;

import com.korres.Page;
import com.korres.Pageable;

@Repository("productNotifyDaoImpl")
public class ProductNotifyDaoImpl extends BaseDaoImpl<ProductNotify, Long>
		implements ProductNotifyDao {
	public boolean exists(Product product, String email) {
		String sql = "select count(*) from ProductNotify productNotify where productNotify.product = :product and lower(productNotify.email) = lower(:email) and productNotify.hasSent = false";
		Long count = (Long) this.entityManager.createQuery(sql, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("product",
						product).setParameter("email", email).getSingleResult();
		return count.longValue() > 0L;
	}

	public Page<ProductNotify> findPage(Member member, Boolean isMarketable,
			Boolean isOutOfStock, Boolean hasSent, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = criteriaBuilder
				.createQuery(ProductNotify.class);
		Root localRoot = localCriteriaQuery.from(ProductNotify.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = criteriaBuilder.conjunction();
		if (member != null)
			localPredicate = criteriaBuilder.and(localPredicate,
					criteriaBuilder.equal(localRoot.get("member"), member));
		if (isMarketable != null)
			localPredicate = criteriaBuilder.and(localPredicate,
					criteriaBuilder.equal(localRoot.get("product").get(
							"isMarketable"), isMarketable));
		if (isOutOfStock != null) {
			Path localPath1 = localRoot.get("product").get("stock");
			Path localPath2 = localRoot.get("product").get("allocatedStock");
			if (isOutOfStock.booleanValue())
				localPredicate = criteriaBuilder.and(new Predicate[] {
						localPredicate,
						criteriaBuilder.isNotNull(localPath1),
						criteriaBuilder.lessThanOrEqualTo(localPath1,
								localPath2) });
			else
				localPredicate = criteriaBuilder.and(localPredicate,
						criteriaBuilder.or(criteriaBuilder.isNull(localPath1),
								criteriaBuilder.greaterThan(localPath1,
										localPath2)));
		}
		if (hasSent != null)
			localPredicate = criteriaBuilder.and(localPredicate,
					criteriaBuilder.equal(localRoot.get("hasSent"), hasSent));
		localCriteriaQuery.where(localPredicate);
		return super.findPage(localCriteriaQuery, pageable);
	}

	public Long count(Member member, Boolean isMarketable,
			Boolean isOutOfStock, Boolean hasSent) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(ProductNotify.class);
		Root localRoot = localCriteriaQuery.from(ProductNotify.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		if (member != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("member"), member));
		if (isMarketable != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("product").get(
							"isMarketable"), isMarketable));
		if (isOutOfStock != null) {
			Path localPath1 = localRoot.get("product").get("stock");
			Path localPath2 = localRoot.get("product").get("allocatedStock");
			if (isOutOfStock.booleanValue())
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localCriteriaBuilder.isNotNull(localPath1),
						localCriteriaBuilder.lessThanOrEqualTo(localPath1,
								localPath2) });
			else
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localCriteriaBuilder
								.isNull(localPath1), localCriteriaBuilder
								.greaterThan(localPath1, localPath2)));
		}
		if (hasSent != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("hasSent"),
							hasSent));
		localCriteriaQuery.where(localPredicate);
		return super.count(localCriteriaQuery, null);
	}
}