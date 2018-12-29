package com.korres.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.korres.dao.MemberDao;
import com.korres.entity.Member;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;

import org.springframework.stereotype.Repository;

import com.korres.Page;
import com.korres.Pageable;

@Repository("memberDaoImpl")
public class MemberDaoImpl extends BaseDaoImpl<Member, Long> implements
		MemberDao {
	public boolean usernameExists(String username) {
		if (username == null)
			return false;
		String str = "select count(*) from Member members where lower(members.username) = lower(:username)";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("username",
						username).getSingleResult();
		return localLong.longValue() > 0L;
	}

	public boolean emailExists(String email) {
		if (email == null)
			return false;
		String str = "select count(*) from Member members where lower(members.email) = lower(:email)";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("email", email).getSingleResult();
		return localLong.longValue() > 0L;
	}

	public Member findByUsername(String username) {
		if (username == null)
			return null;
		try {
			String str = "select members from Member members where lower(members.username) = lower(:username)";
			return (Member) this.entityManager.createQuery(str, Member.class)
					.setFlushMode(FlushModeType.COMMIT).setParameter(
							"username", username).getSingleResult();
		} catch (NoResultException localNoResultException1) {
		}
		return null;
	}

	public List<Member> findListByEmail(String email) {
		if (email == null)
			return Collections.emptyList();
		String str = "select members from Member members where lower(members.email) = lower(:email)";
		return this.entityManager.createQuery(str, Member.class).setFlushMode(
				FlushModeType.COMMIT).setParameter("email", email)
				.getResultList();
	}

	public Page<Object> findPurchasePage(Date beginDate, Date endDate,
			Pageable pageable) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery1 = localCriteriaBuilder
				.createQuery(Object.class);
		Root localRoot1 = localCriteriaQuery1.from(Member.class);
		Join localJoin1 = localRoot1.join("orders");
		localCriteriaQuery1.multiselect(new Selection[] { localRoot1,
				localCriteriaBuilder.sum(localJoin1.get("amountPaid")) });
		Predicate localPredicate1 = localCriteriaBuilder.conjunction();
		if (beginDate != null)
			localPredicate1 = localCriteriaBuilder.and(localPredicate1,
					localCriteriaBuilder.greaterThanOrEqualTo(localJoin1
							.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate1 = localCriteriaBuilder.and(localPredicate1,
					localCriteriaBuilder.lessThanOrEqualTo(localJoin1
							.get("createDate"), endDate));
		localPredicate1 = localCriteriaBuilder.and(localPredicate1,
				localCriteriaBuilder.equal(localJoin1.get("orderStatus"),
						OrderOrderStatus.completed));
		localPredicate1 = localCriteriaBuilder.and(localPredicate1,
				localCriteriaBuilder.equal(localJoin1.get("paymentStatus"),
						OrderPaymentStatus.paid));
		localCriteriaQuery1.where(localPredicate1);
		localCriteriaQuery1.groupBy(new Expression[] { localRoot1.get("id") });
		CriteriaQuery localCriteriaQuery2 = localCriteriaBuilder
				.createQuery(Long.class);
		Root localRoot2 = localCriteriaQuery2.from(Member.class);
		Join localJoin2 = localRoot2.join("orders");
		localCriteriaQuery2.select(localCriteriaBuilder
				.countDistinct(localRoot2));
		Predicate localPredicate2 = localCriteriaBuilder.conjunction();
		if (beginDate != null)
			localPredicate2 = localCriteriaBuilder.and(localPredicate2,
					localCriteriaBuilder.greaterThanOrEqualTo(localJoin2
							.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate2 = localCriteriaBuilder.and(localPredicate2,
					localCriteriaBuilder.lessThanOrEqualTo(localJoin2
							.get("createDate"), endDate));
		localPredicate2 = localCriteriaBuilder.and(localPredicate2,
				localCriteriaBuilder.equal(localJoin2.get("orderStatus"),
						OrderOrderStatus.completed));
		localCriteriaQuery2.where(localPredicate2);
		Long localLong = (Long) this.entityManager.createQuery(
				localCriteriaQuery2).setFlushMode(FlushModeType.COMMIT)
				.getSingleResult();
		int i = (int) Math.ceil(localLong.longValue() / pageable.getPageSize());
		if (i < pageable.getPageNumber())
			pageable.setPageNumber(i);
		localCriteriaQuery1
				.orderBy(new Order[] { localCriteriaBuilder
						.desc(localCriteriaBuilder.sum(localJoin1
								.get("amountPaid"))) });
		TypedQuery localTypedQuery = this.entityManager.createQuery(
				localCriteriaQuery1).setFlushMode(FlushModeType.COMMIT);
		localTypedQuery.setFirstResult((pageable.getPageNumber() - 1)
				* pageable.getPageSize());
		localTypedQuery.setMaxResults(pageable.getPageSize());
		return new Page(localTypedQuery.getResultList(), localLong.longValue(),
				pageable);
	}
}