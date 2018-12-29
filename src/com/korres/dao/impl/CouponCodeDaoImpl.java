package com.korres.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.korres.dao.CouponCodeDao;
import com.korres.entity.Coupon;
import com.korres.entity.CouponCode;
import com.korres.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.korres.Page;
import com.korres.Pageable;

@Repository("couponCodeDaoImpl")
public class CouponCodeDaoImpl extends BaseDaoImpl<CouponCode, Long> implements
		CouponCodeDao {
	public boolean codeExists(String code) {
		if (code == null)
			return false;
		String str = "select count(*) from CouponCode couponCode where lower(couponCode.code) = lower(:code)";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("code", code)
				.getSingleResult();
		return localLong.longValue() > 0L;
	}

	public CouponCode findByCode(String code) {
		if (code == null)
			return null;
		try {
			String str = "select couponCode from CouponCode couponCode where lower(couponCode.code) = lower(:code)";
			return (CouponCode) this.entityManager.createQuery(str,
					CouponCode.class).setFlushMode(FlushModeType.COMMIT)
					.setParameter("code", code).getSingleResult();
		} catch (NoResultException localNoResultException1) {
		}
		return null;
	}

	public CouponCode build(Coupon coupon, Member member) {
		Assert.notNull(coupon);
		CouponCode localCouponCode = new CouponCode();
		String str = UUID.randomUUID().toString().toUpperCase();
		localCouponCode.setCode(coupon.getPrefix() + str.substring(0, 8)
				+ str.substring(9, 13) + str.substring(14, 18)
				+ str.substring(19, 23) + str.substring(24));
		localCouponCode.setIsUsed(Boolean.valueOf(false));
		localCouponCode.setCoupon(coupon);
		localCouponCode.setMember(member);
		super.persist(localCouponCode);
		return localCouponCode;
	}

	public List<CouponCode> build(Coupon coupon, Member member, Integer count) {
		Assert.notNull(coupon);
		Assert.notNull(count);
		ArrayList localArrayList = new ArrayList();
		for (int i = 0; i < count.intValue(); i++) {
			CouponCode localCouponCode = build(coupon, member);
			localArrayList.add(localCouponCode);
			if (i % 20 == 0) {
				super.flush();
				super.clear();
			}
		}
		return localArrayList;
	}

	public Page<CouponCode> findPage(Member member, Pageable pageable) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(CouponCode.class);
		Root localRoot = localCriteriaQuery.from(CouponCode.class);
		localCriteriaQuery.select(localRoot);
		if (member != null)
			localCriteriaQuery.where(localCriteriaBuilder.equal(localRoot
					.get("member"), member));
		return super.findPage(localCriteriaQuery, pageable);
	}

	public Long count(Coupon coupon, Member member, Boolean hasBegun,
			Boolean hasExpired, Boolean isUsed) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(CouponCode.class);
		Root localRoot = localCriteriaQuery.from(CouponCode.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		Path localPath = localRoot.get("coupon");
		if (coupon != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localPath, coupon));
		if (member != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("member"), member));
		if (hasBegun != null)
			if (hasBegun.booleanValue())
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localPath.get("beginDate")
								.isNull(), localCriteriaBuilder
								.lessThanOrEqualTo(localPath.get("beginDate"),
										new Date())));
			else
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localPath.get("beginDate").isNotNull(),
						localCriteriaBuilder.greaterThan(localPath
								.get("beginDate"), new Date()) });
		if (hasExpired != null)
			if (hasExpired.booleanValue())
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localPath.get("endDate").isNotNull(),
						localCriteriaBuilder.lessThan(localPath.get("endDate"),
								new Date()) });
			else
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localPath.get("endDate")
								.isNull(), localCriteriaBuilder
								.greaterThanOrEqualTo(localPath.get("endDate"),
										new Date())));
		if (isUsed != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("isUsed"), isUsed));
		localCriteriaQuery.where(localPredicate);
		return super.count(localCriteriaQuery, null);
	}
}