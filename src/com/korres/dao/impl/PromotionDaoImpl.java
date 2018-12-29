package com.korres.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.korres.dao.PromotionDao;
import com.korres.entity.Promotion;

import org.springframework.stereotype.Repository;

import com.korres.Filter;
import com.korres.Order;

@Repository("promotionDaoImpl")
public class PromotionDaoImpl extends BaseDaoImpl<Promotion, Long> implements
		PromotionDao {
	public List<Promotion> findList(Boolean hasBegun, Boolean hasEnded,
			Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder
				.createQuery(Promotion.class);
		Root root = criteriaQuery.from(Promotion.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		if (hasBegun != null) {
			if (hasBegun.booleanValue()) {
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
						root.get("beginDate").isNull(), criteriaBuilder
								.lessThanOrEqualTo(root.get("beginDate"),
										new Date())));
			} else {
				predicate = criteriaBuilder.and(new Predicate[] {
						predicate,
						root.get("beginDate").isNotNull(),
						criteriaBuilder.greaterThan(root.get("beginDate"),
								new Date()) });
			}
		}

		if (hasEnded != null) {
			if (hasEnded.booleanValue())
				predicate = criteriaBuilder.and(new Predicate[] {
						predicate,
						root.get("endDate").isNotNull(),
						criteriaBuilder.lessThan(root.get("endDate"),
								new Date()) });
			else
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
						root.get("endDate").isNull(), criteriaBuilder
								.greaterThanOrEqualTo(root.get("endDate"),
										new Date())));
		}

		criteriaQuery.where(predicate);

		return super.findList(criteriaQuery, null, count, filters, orders);
	}
}