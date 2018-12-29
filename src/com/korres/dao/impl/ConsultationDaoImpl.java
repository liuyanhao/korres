package com.korres.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;
import com.korres.dao.ConsultationDao;
import com.korres.entity.Consultation;
import com.korres.entity.Member;
import com.korres.entity.Product;

/*
 * 类名：ConsultationDaoImpl.java
 * 功能说明：商品咨询dao实现类
 * 创建日期：2013-12-20 下午05:02:33
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Repository("consultationDaoImpl")
public class ConsultationDaoImpl extends BaseDaoImpl<Consultation, Long>
		implements ConsultationDao {
	public List<Consultation> findList(Member member, Product product,
			Boolean isShow, Integer count, List<Filter> filters,
			List<Order> orders) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder
				.createQuery(Consultation.class);
		Root localRoot = criteriaQuery.from(Consultation.class);
		criteriaQuery.select(localRoot);
		Predicate predicate = criteriaBuilder.conjunction();
		predicate = criteriaBuilder.and(predicate, criteriaBuilder
				.isNull(localRoot.get("forConsultation")));
		if (member != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					localRoot.get("member"), member));
		}

		if (product != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					localRoot.get("product"), product));
		}

		if (isShow != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					localRoot.get("isShow"), isShow));
		}

		criteriaQuery.where(predicate);

		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public Page<Consultation> findPage(Member member, Product product,
			Boolean isShow, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder
				.createQuery(Consultation.class);
		Root root = criteriaQuery.from(Consultation.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root
				.get("forConsultation")));
		if (member != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("member"), member));
		}

		if (product != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("product"), product));
		}

		if (isShow != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("isShow"), isShow));
		}

		criteriaQuery.where(predicate);

		return super.findPage(criteriaQuery, pageable);
	}

	public Long count(Member member, Product product, Boolean isShow) {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder
				.createQuery(Consultation.class);
		Root root = criteriaQuery.from(Consultation.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root
				.get("forConsultation")));
		if (member != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("member"), member));
		}

		if (product != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("product"), product));
		}

		if (isShow != null) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
					root.get("isShow"), isShow));
		}

		criteriaQuery.where(predicate);

		return super.count(criteriaQuery, null);
	}
}