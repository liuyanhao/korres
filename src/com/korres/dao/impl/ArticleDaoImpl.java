package com.korres.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.korres.dao.ArticleDao;
import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.entity.Tag;

import org.springframework.stereotype.Repository;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;

@Repository("articleDaoImpl")
public class ArticleDaoImpl extends BaseDaoImpl<Article, Long> implements
		ArticleDao {
	public List<Article> findList(ArticleCategory articleCategory,
			List<Tag> tags, Integer count, List<Filter> filters,
			List<com.korres.Order> orders) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Article.class);
		Root localRoot = localCriteriaQuery.from(Article.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("isPublication"),
						Boolean.valueOf(true)));
		if (articleCategory != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.or(localCriteriaBuilder.equal(
							localRoot.get("articleCategory"), articleCategory),
							localCriteriaBuilder.like(localRoot.get(
									"articleCategory").get("treePath"), "%,"
									+ articleCategory.getId() + "," + "%")));
		if ((tags != null) && (!tags.isEmpty())) {
			localPredicate = localCriteriaBuilder.and(localPredicate, localRoot
					.join("tags").in(tags));
			localCriteriaQuery.distinct(true);
		}
		localCriteriaQuery.where(localPredicate);
		localCriteriaQuery
				.orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
						.desc(localRoot.get("isTop")) });
		return super.findList(localCriteriaQuery, null, count, filters, orders);
	}

	public List<Article> findList(ArticleCategory articleCategory,
			Date beginDate, Date endDate, Integer first, Integer count) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Article.class);
		Root localRoot = localCriteriaQuery.from(Article.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("isPublication"),
						Boolean.valueOf(true)));
		if (articleCategory != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.or(localCriteriaBuilder.equal(
							localRoot.get("articleCategory"), articleCategory),
							localCriteriaBuilder.like(localRoot.get(
									"articleCategory").get("treePath"), "%,"
									+ articleCategory.getId() + "," + "%")));
		if (beginDate != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.greaterThanOrEqualTo(localRoot
							.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.lessThanOrEqualTo(localRoot
							.get("createDate"), endDate));
		localCriteriaQuery.where(localPredicate);
		localCriteriaQuery
				.orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
						.desc(localRoot.get("isTop")) });
		return super.findList(localCriteriaQuery, first, count, null, null);
	}

	public Page<Article> findPage(ArticleCategory articleCategory,
			List<Tag> tags, Pageable pageable) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Article.class);
		Root localRoot = localCriteriaQuery.from(Article.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("isPublication"),
						Boolean.valueOf(true)));
		if (articleCategory != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.or(localCriteriaBuilder.equal(
							localRoot.get("articleCategory"), articleCategory),
							localCriteriaBuilder.like(localRoot.get(
									"articleCategory").get("treePath"), "%,"
									+ articleCategory.getId() + "," + "%")));
		if ((tags != null) && (!tags.isEmpty())) {
			localPredicate = localCriteriaBuilder.and(localPredicate, localRoot
					.join("tags").in(tags));
			localCriteriaQuery.distinct(true);
		}
		localCriteriaQuery.where(localPredicate);
		localCriteriaQuery
				.orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
						.desc(localRoot.get("isTop")) });
		return super.findPage(localCriteriaQuery, pageable);
	}
}