package com.korres.dao.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.korres.dao.OrderDao;
import com.korres.entity.Member;
import com.korres.entity.OrderItem;
import com.korres.entity.Product;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Order.OrderShippingStatus;

import org.springframework.stereotype.Repository;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;

@Repository("orderDaoImpl")
public class OrderDaoImpl extends BaseDaoImpl<com.korres.entity.Order, Long>
		implements OrderDao {
	public com.korres.entity.Order findBySn(String sn) {
		if (sn == null)
			return null;
		String str = "select orders from Order orders where lower(orders.sn) = lower(:sn)";
		try {
			return (com.korres.entity.Order) this.entityManager.createQuery(
					str, com.korres.entity.Order.class).setFlushMode(
					FlushModeType.COMMIT).setParameter("sn", sn)
					.getSingleResult();
		} catch (NoResultException localNoResultException) {
		}
		return null;
	}

	public List<com.korres.entity.Order> findList(Member member, Integer count,
			List<Filter> filters, List<com.korres.Order> orders) {
		if (member == null)
			return Collections.emptyList();
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(com.korres.entity.Order.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localRoot);
		localCriteriaQuery.where(localCriteriaBuilder.equal(localRoot
				.get("member"), member));
		return super.findList(localCriteriaQuery, null, count, filters, orders);
	}

	public Page<com.korres.entity.Order> findPage(Member member,
			Pageable pageable) {
		if (member == null)
			return new Page(Collections.emptyList(), 0L, pageable);
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(com.korres.entity.Order.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localRoot);
		localCriteriaQuery.where(localCriteriaBuilder.equal(localRoot
				.get("member"), member));
		return super.findPage(localCriteriaQuery, pageable);
	}

	public Page<com.korres.entity.Order> findPage(OrderOrderStatus orderStatus,
			OrderPaymentStatus paymentStatus,
			OrderShippingStatus shippingStatus, Boolean hasExpired,
			Pageable pageable) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(com.korres.entity.Order.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		if (orderStatus != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("orderStatus"),
							orderStatus));
		if (paymentStatus != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("paymentStatus"),
							paymentStatus));
		if (shippingStatus != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("shippingStatus"),
							shippingStatus));
		if (hasExpired != null)
			if (hasExpired.booleanValue())
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localRoot.get("expire").isNotNull(),
						localCriteriaBuilder.lessThan(localRoot.get("expire"),
								new Date()) });
			else
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localRoot.get("expire")
								.isNull(), localCriteriaBuilder
								.greaterThanOrEqualTo(localRoot.get("expire"),
										new Date())));
		localCriteriaQuery.where(localPredicate);
		return super.findPage(localCriteriaQuery, pageable);
	}

	public Long count(OrderOrderStatus orderStatus,
			OrderPaymentStatus paymentStatus,
			OrderShippingStatus shippingStatus, Boolean hasExpired) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(com.korres.entity.Order.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		if (orderStatus != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("orderStatus"),
							orderStatus));
		if (paymentStatus != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("paymentStatus"),
							paymentStatus));
		if (shippingStatus != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("shippingStatus"),
							shippingStatus));
		if (hasExpired != null)
			if (hasExpired.booleanValue())
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localRoot.get("expire").isNotNull(),
						localCriteriaBuilder.lessThan(localRoot.get("expire"),
								new Date()) });
			else
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localRoot.get("expire")
								.isNull(), localCriteriaBuilder
								.greaterThanOrEqualTo(localRoot.get("expire"),
										new Date())));
		localCriteriaQuery.where(localPredicate);
		return super.count(localCriteriaQuery, null);
	}

	public Long waitingPaymentCount(Member member) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(com.korres.entity.Order.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(new Predicate[] {
				localPredicate,
				localCriteriaBuilder.notEqual(localRoot.get("orderStatus"),
						OrderOrderStatus.completed),
				localCriteriaBuilder.notEqual(localRoot.get("orderStatus"),
						OrderOrderStatus.cancelled) });
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.or(localCriteriaBuilder.equal(localRoot
						.get("paymentStatus"), OrderPaymentStatus.unpaid),
						localCriteriaBuilder.equal(localRoot
								.get("paymentStatus"),
								OrderPaymentStatus.partialPayment)));
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.or(localRoot.get("expire").isNull(),
						localCriteriaBuilder.greaterThanOrEqualTo(localRoot
								.get("expire"), new Date())));
		if (member != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("member"), member));
		localCriteriaQuery.where(localPredicate);
		return super.count(localCriteriaQuery, null);
	}

	public Long waitingShippingCount(Member member) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(com.korres.entity.Order.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(new Predicate[] {
				localPredicate,
				localCriteriaBuilder.notEqual(localRoot.get("orderStatus"),
						OrderOrderStatus.completed),
				localCriteriaBuilder.notEqual(localRoot.get("orderStatus"),
						OrderOrderStatus.cancelled),
				localCriteriaBuilder.equal(localRoot.get("paymentStatus"),
						OrderPaymentStatus.paid),
				localCriteriaBuilder.equal(localRoot.get("shippingStatus"),
						OrderShippingStatus.unshipped) });
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.or(localRoot.get("expire").isNull(),
						localCriteriaBuilder.greaterThanOrEqualTo(localRoot
								.get("expire"), new Date())));
		if (member != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("member"), member));
		localCriteriaQuery.where(localPredicate);
		return super.count(localCriteriaQuery, null);
	}

	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(BigDecimal.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localCriteriaBuilder.sum(localRoot
				.get("amountPaid")));
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("orderStatus"),
						OrderOrderStatus.completed));
		if (beginDate != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.greaterThanOrEqualTo(localRoot
							.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.lessThanOrEqualTo(localRoot
							.get("createDate"), endDate));
		localCriteriaQuery.where(localPredicate);
		return (BigDecimal) this.entityManager.createQuery(localCriteriaQuery)
				.setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	public Integer getSalesVolume(Date beginDate, Date endDate) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Integer.class);
		Root localRoot = localCriteriaQuery.from(com.korres.entity.Order.class);
		localCriteriaQuery.select(localCriteriaBuilder.sum(localRoot.join(
				"orderItems").get("shippedQuantity")));
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("orderStatus"),
						OrderOrderStatus.completed));
		if (beginDate != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.greaterThanOrEqualTo(localRoot
							.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.lessThanOrEqualTo(localRoot
							.get("createDate"), endDate));
		localCriteriaQuery.where(localPredicate);
		return (Integer) this.entityManager.createQuery(localCriteriaQuery)
				.setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	public void releaseStock() {
		String str = "select orders from Order orders where orders.isAllocatedStock = :isAllocatedStock and orders.expire is not null and orders.expire <= :now";
		List localList = this.entityManager.createQuery(str,
				com.korres.entity.Order.class).setParameter("isAllocatedStock",
				Boolean.valueOf(true)).setParameter("now", new Date())
				.getResultList();
		if (localList != null) {
			Iterator localIterator1 = localList.iterator();
			while (localIterator1.hasNext()) {
				com.korres.entity.Order localOrder = (com.korres.entity.Order) localIterator1
						.next();
				if ((localOrder != null)
						&& (localOrder.getOrderItems() != null)) {
					Iterator localIterator2 = localOrder.getOrderItems()
							.iterator();
					while (localIterator2.hasNext()) {
						OrderItem localOrderItem = (OrderItem) localIterator2
								.next();
						if (localOrderItem != null) {
							Product localProduct = localOrderItem.getProduct();
							if (localProduct != null) {
								this.entityManager.lock(localProduct,
										LockModeType.PESSIMISTIC_WRITE);
								localProduct
										.setAllocatedStock(Integer
												.valueOf(localProduct
														.getAllocatedStock()
														.intValue()
														- (localOrderItem
																.getQuantity()
																.intValue() - localOrderItem
																.getShippedQuantity()
																.intValue())));
							}
						}
					}
					localOrder.setIsAllocatedStock(Boolean.valueOf(false));
				}
			}
		}
	}
}