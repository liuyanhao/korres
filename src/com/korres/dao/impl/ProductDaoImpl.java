package com.korres.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.korres.dao.GoodsDao;
import com.korres.dao.ProductDao;
import com.korres.dao.SnDao;
import com.korres.entity.Attribute;
import com.korres.entity.Brand;
import com.korres.entity.Goods;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.ProductCategory;
import com.korres.entity.Promotion;
import com.korres.entity.SpecificationValue;
import com.korres.entity.Tag;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Product.ProductOrderType;
import com.korres.entity.Sn.SnType;
import com.korres.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;
import com.korres.Setting;

@Repository("productDaoImpl")
public class ProductDaoImpl extends BaseDaoImpl<Product, Long> implements
		ProductDao {
	private static final Pattern IIIllIll = Pattern.compile("\\d*");

	@Resource(name = "goodsDaoImpl")
	private GoodsDao goodsDao;

	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	public boolean snExists(String sn) {
		if (sn == null) {
			return false;
		}

		String str = "select count(*) from Product product where lower(product.sn) = lower(:sn)";
		Long count = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn)
				.getSingleResult();

		return count.longValue() > 0L;
	}

	public Product findBySn(String sn) {
		if (sn == null) {
			return null;
		}

		String str = "select product from Product product where lower(product.sn) = lower(:sn)";
		try {
			return (Product) this.entityManager.createQuery(str, Product.class)
					.setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn)
					.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.emptyList();
		}

		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate predicate = criteriaBuilder.conjunction();
		
		if (IIIllIll.matcher(keyword).matches()){
			predicate = criteriaBuilder.and(predicate,
					criteriaBuilder.or(new Predicate[] {
							criteriaBuilder.equal(root.get("id"),
									Long.valueOf(keyword)),
									criteriaBuilder.like(root.get("sn"), "%"
									+ keyword + "%"),
									criteriaBuilder.like(
											root.get("fullName"), "%" + keyword
											+ "%") }));
		}
		else{
			predicate = criteriaBuilder.and(predicate,
					criteriaBuilder.or(criteriaBuilder.like(root
							.get("sn"), "%" + keyword + "%"),
							criteriaBuilder.like(
									root.get("fullName"), "%" + keyword
											+ "%")));
		}
		
		if (isGift != null){
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		
		criteriaQuery.where(predicate);
		criteriaQuery.orderBy(new javax.persistence.criteria.Order[] { criteriaBuilder.desc(root.get("isTop")) });
		
		return super.findList(criteriaQuery, null, count, null, null);
	}

	public List<Product> findList(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, ProductOrderType orderType, Integer count,
			List<Filter> filters, List<com.korres.Order> orders) {

		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Product.class);
		Root localRoot = localCriteriaQuery.from(Product.class);
		localCriteriaQuery.select(localRoot);
		Predicate predicate = localCriteriaBuilder.conjunction();
		if (productCategory != null) {
			predicate = localCriteriaBuilder.and(predicate,
					localCriteriaBuilder.or(localCriteriaBuilder.equal(
							localRoot.get("productCategory"), productCategory),
							localCriteriaBuilder.like(localRoot.get(
									"productCategory").get("treePath"), "%,"
									+ productCategory.getId() + "," + "%")));
		}

		if (brand != null) {
			predicate = localCriteriaBuilder.and(predicate,
					localCriteriaBuilder.equal(localRoot.get("brand"), brand));
		}

		if (promotion != null) {
			predicate = localCriteriaBuilder.and(predicate,
					localCriteriaBuilder.or(new Predicate[] {
							localCriteriaBuilder.equal(localRoot.join(
									"promotions", JoinType.LEFT), promotion),
							localCriteriaBuilder.equal(localRoot.join(
									"productCategory", JoinType.LEFT).join(
									"promotions", JoinType.LEFT), promotion),
							localCriteriaBuilder.equal(localRoot.join("brand",
									JoinType.LEFT).join("promotions",
									JoinType.LEFT), promotion) }));
		}

		if (attributeValue != null) {
			Iterator iterator = attributeValue.entrySet().iterator();
			while (iterator.hasNext()) {
				Object localObject1 = iterator.next();
				String localObject3 = "attributeValue"
						+ ((Attribute) ((Map.Entry) localObject1).getKey())
								.getPropertyIndex();
				predicate = localCriteriaBuilder.and(predicate,
						localCriteriaBuilder.equal(localRoot.get(localObject3),
								((Map.Entry) localObject1).getValue()));
			}
		}
		if ((startPrice != null) && (endPrice != null)
				&& (startPrice.compareTo(endPrice) > 0)) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = (BigDecimal) temp;
		}
		if ((startPrice != null)
				&& (startPrice.compareTo(new BigDecimal(0)) >= 0)) {
			predicate = localCriteriaBuilder
					.and(predicate, localCriteriaBuilder.ge(localRoot
							.get("price"), startPrice));
		}
		if ((endPrice != null) && (endPrice.compareTo(new BigDecimal(0)) >= 0)) {
			predicate = localCriteriaBuilder.and(predicate,
					localCriteriaBuilder.le(localRoot.get("price"), endPrice));
		}
		if ((tags != null) && (!tags.isEmpty())) {
			predicate = localCriteriaBuilder.and(predicate, localRoot.join(
					"tags").in(tags));
			localCriteriaQuery.distinct(true);
		}
		if (isMarketable != null) {
			predicate = localCriteriaBuilder.and(predicate,
					localCriteriaBuilder.equal(localRoot.get("isMarketable"),
							isMarketable));
		}
		if (isList != null) {
			predicate = localCriteriaBuilder
					.and(predicate, localCriteriaBuilder.equal(localRoot
							.get("isList"), isList));
		}
		if (isTop != null) {
			predicate = localCriteriaBuilder.and(predicate,
					localCriteriaBuilder.equal(localRoot.get("isTop"), isTop));
		}
		if (isGift != null) {
			predicate = localCriteriaBuilder
					.and(predicate, localCriteriaBuilder.equal(localRoot
							.get("isGift"), isGift));
		}

		Object localObject1 = localRoot.get("stock");
		Object localObject2 = localRoot.get("allocatedStock");
		if (isOutOfStock != null)
			if (isOutOfStock.booleanValue())
				predicate = localCriteriaBuilder.and(new Predicate[] {
						predicate,
						localCriteriaBuilder
								.isNotNull((Expression) localObject1),
						localCriteriaBuilder.lessThanOrEqualTo(
								(Expression) localObject1,
								(Expression) localObject2) });
			else
				predicate = localCriteriaBuilder.and(predicate,
						localCriteriaBuilder.or(localCriteriaBuilder
								.isNull((Expression) localObject1),
								localCriteriaBuilder.greaterThan(
										(Expression) localObject1,
										(Expression) localObject2)));
		if (isStockAlert != null) {
			Setting localObject3 = SettingUtils.get();
			if (isStockAlert.booleanValue())
				predicate = localCriteriaBuilder
						.and(new Predicate[] {
								predicate,
								localCriteriaBuilder
										.isNotNull((Expression) localObject1),
								localCriteriaBuilder.lessThanOrEqualTo(
										(Expression) localObject1,
										localCriteriaBuilder.sum(
												(Expression) localObject2,
												localObject3
														.getStockAlertCount())) });
			else
				predicate = localCriteriaBuilder
						.and(
								predicate,
								localCriteriaBuilder
										.or(
												localCriteriaBuilder
														.isNull((Expression) localObject1),
												localCriteriaBuilder
														.greaterThan(
																(Expression) localObject1,
																localCriteriaBuilder
																		.sum(
																				(Expression) localObject2,
																				localObject3
																						.getStockAlertCount()))));
		}
		localCriteriaQuery.where(predicate);
		if (orderType == ProductOrderType.priceAsc) {
			orders.add(com.korres.Order.asc("price"));
			orders.add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.priceDesc) {
			orders.add(com.korres.Order.desc("price"));
			orders.add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.salesDesc) {
			orders.add(com.korres.Order.desc("sales"));
			orders.add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.scoreDesc) {
			orders.add(com.korres.Order.desc("score"));
			orders.add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.dateDesc) {
			orders.add(com.korres.Order.desc("createDate"));
		} else {
			orders.add(com.korres.Order.desc("isTop"));
			orders.add(com.korres.Order.desc("modifyDate"));
		}
		return super.findList(localCriteriaQuery, null, count, filters, orders);
	}

	public List<Product> findList(ProductCategory productCategory,
			Date beginDate, Date endDate, Integer first, Integer count) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Product.class);
		Root localRoot = localCriteriaQuery.from(Product.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		localPredicate = localCriteriaBuilder.and(localPredicate,
				localCriteriaBuilder.equal(localRoot.get("isMarketable"),
						Boolean.valueOf(true)));
		if (productCategory != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.or(localCriteriaBuilder.equal(
							localRoot.get("productCategory"), productCategory),
							localCriteriaBuilder.like(localRoot.get(
									"productCategory").get("treePath"), "%,"
									+ productCategory.getId() + "," + "%")));
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

	public List<Product> findList(Goods goods, Set<Product> excludes) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Product.class);
		Root localRoot = localCriteriaQuery.from(Product.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		if (goods != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("goods"), goods));
		if ((excludes != null) && (!excludes.isEmpty()))
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.not(localRoot.in(excludes)));
		localCriteriaQuery.where(localPredicate);
		return this.entityManager.createQuery(localCriteriaQuery).setFlushMode(
				FlushModeType.COMMIT).getResultList();
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand,
			Promotion promotion, List<Tag> tags,
			Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock,
			Boolean isStockAlert, ProductOrderType orderType, Pageable pageable) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Product.class);
		Root localRoot = localCriteriaQuery.from(Product.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		if (productCategory != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.or(localCriteriaBuilder.equal(
							localRoot.get("productCategory"), productCategory),
							localCriteriaBuilder.like(localRoot.get(
									"productCategory").get("treePath"), "%,"
									+ productCategory.getId() + "," + "%")));
		if (brand != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("brand"), brand));
		if (promotion != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.or(new Predicate[] {
							localCriteriaBuilder.equal(localRoot.join(
									"promotions", JoinType.LEFT), promotion),
							localCriteriaBuilder.equal(localRoot.join(
									"productCategory", JoinType.LEFT).join(
									"promotions", JoinType.LEFT), promotion),
							localCriteriaBuilder.equal(localRoot.join("brand",
									JoinType.LEFT).join("promotions",
									JoinType.LEFT), promotion) }));
		if ((tags != null) && (!tags.isEmpty())) {
			localPredicate = localCriteriaBuilder.and(localPredicate, localRoot
					.join("tags").in(tags));
			localCriteriaQuery.distinct(true);
		}
		if (attributeValue != null) {
			Iterator iterator = attributeValue.entrySet().iterator();
			while (iterator.hasNext()) {
				Object localObject1 = iterator.next();
				String localObject3 = "attributeValue"
						+ ((Attribute) ((Map.Entry) localObject1).getKey())
								.getPropertyIndex();
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.equal(localRoot.get(localObject3),
								((Map.Entry) localObject1).getValue()));
			}
		}
		if ((startPrice != null) && (endPrice != null)
				&& (startPrice.compareTo(endPrice) > 0)) {
			BigDecimal localObject1 = startPrice;
			startPrice = endPrice;
			endPrice = localObject1;
		}
		if ((startPrice != null)
				&& (startPrice.compareTo(new BigDecimal(0)) >= 0))
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.ge(localRoot
							.get("price"), startPrice));
		if ((endPrice != null) && (endPrice.compareTo(new BigDecimal(0)) >= 0))
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.le(localRoot.get("price"), endPrice));
		if (isMarketable != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("isMarketable"),
							isMarketable));
		if (isList != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("isList"), isList));
		if (isTop != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("isTop"), isTop));
		if (isGift != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("isGift"), isGift));
		Object localObject1 = localRoot.get("stock");
		Object localObject2 = localRoot.get("allocatedStock");
		if (isOutOfStock != null)
			if (isOutOfStock.booleanValue())
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localCriteriaBuilder
								.isNotNull((Expression) localObject1),
						localCriteriaBuilder.lessThanOrEqualTo(
								(Expression) localObject1,
								(Expression) localObject2) });
			else
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localCriteriaBuilder
								.isNull((Expression) localObject1),
								localCriteriaBuilder.greaterThan(
										(Expression) localObject1,
										(Expression) localObject2)));
		if (isStockAlert != null) {
			Setting localObject3 = SettingUtils.get();
			if (isStockAlert.booleanValue())
				localPredicate = localCriteriaBuilder
						.and(new Predicate[] {
								localPredicate,
								localCriteriaBuilder
										.isNotNull((Expression) localObject1),
								localCriteriaBuilder.lessThanOrEqualTo(
										(Expression) localObject1,
										localCriteriaBuilder.sum(
												(Expression) localObject2,
												localObject3
														.getStockAlertCount())) });
			else
				localPredicate = localCriteriaBuilder
						.and(
								localPredicate,
								localCriteriaBuilder
										.or(
												localCriteriaBuilder
														.isNull((Expression) localObject1),
												localCriteriaBuilder
														.greaterThan(
																(Expression) localObject1,
																localCriteriaBuilder
																		.sum(
																				(Expression) localObject2,
																				localObject3
																						.getStockAlertCount()))));
		}
		localCriteriaQuery.where(localPredicate);
		Object localObject3 = pageable.getOrders();
		if (orderType == ProductOrderType.priceAsc) {
			((List) localObject3).add(com.korres.Order.asc("price"));
			((List) localObject3).add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.priceDesc) {
			((List) localObject3).add(com.korres.Order.desc("price"));
			((List) localObject3).add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.salesDesc) {
			((List) localObject3).add(com.korres.Order.desc("sales"));
			((List) localObject3).add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.scoreDesc) {
			((List) localObject3).add(com.korres.Order.desc("score"));
			((List) localObject3).add(com.korres.Order.desc("createDate"));
		} else if (orderType == ProductOrderType.dateDesc) {
			((List) localObject3).add(com.korres.Order.desc("createDate"));
		} else {
			((List) localObject3).add(com.korres.Order.desc("isTop"));
			((List) localObject3).add(com.korres.Order.desc("modifyDate"));
		}
		return super.findPage(localCriteriaQuery, pageable);
	}

	public Page<Product> findPage(Member member, Pageable pageable) {
		if (member == null)
			return new Page(Collections.emptyList(), 0L, pageable);
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Product.class);
		Root localRoot = localCriteriaQuery.from(Product.class);
		localCriteriaQuery.select(localRoot);
		localCriteriaQuery.where(localCriteriaBuilder.equal(localRoot
				.join("favoriteMembers"), member));
		return super.findPage(localCriteriaQuery, pageable);
	}

	public Page<Object> findSalesPage(Date beginDate, Date endDate,
			Pageable pageable) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery1 = localCriteriaBuilder
				.createQuery(Object.class);
		Root localRoot1 = localCriteriaQuery1.from(Product.class);
		Join localJoin1 = localRoot1.join("orderItems");
		Join localJoin2 = localJoin1.join("order");
		localCriteriaQuery1.multiselect(new Selection[] {
				localRoot1,
				localCriteriaBuilder.sum(localJoin1.get("quantity")),
				localCriteriaBuilder.sum(localCriteriaBuilder.prod(localJoin1
						.get("quantity"), localJoin1.get("price"))) });
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
				localCriteriaBuilder.equal(localJoin2.get("orderStatus"),
						OrderOrderStatus.completed));
		localPredicate1 = localCriteriaBuilder.and(localPredicate1,
				localCriteriaBuilder.equal(localJoin2.get("paymentStatus"),
						OrderPaymentStatus.paid));
		localCriteriaQuery1.where(localPredicate1);
		localCriteriaQuery1.groupBy(new Expression[] { localRoot1.get("id") });
		CriteriaQuery localCriteriaQuery2 = localCriteriaBuilder
				.createQuery(Long.class);
		Root localRoot2 = localCriteriaQuery2.from(Product.class);
		Join localJoin3 = localRoot2.join("orderItems");
		Join localJoin4 = localJoin3.join("order");
		Predicate localPredicate2 = localCriteriaBuilder.conjunction();
		if (beginDate != null)
			localPredicate2 = localCriteriaBuilder.and(localPredicate2,
					localCriteriaBuilder.greaterThanOrEqualTo(localJoin3
							.get("createDate"), beginDate));
		if (endDate != null)
			localPredicate2 = localCriteriaBuilder.and(localPredicate2,
					localCriteriaBuilder.lessThanOrEqualTo(localJoin3
							.get("createDate"), endDate));
		localPredicate2 = localCriteriaBuilder.and(localPredicate2,
				localCriteriaBuilder.equal(localJoin4.get("orderStatus"),
						OrderOrderStatus.completed));
		localCriteriaQuery2.select(localCriteriaBuilder
				.countDistinct(localRoot2));
		localCriteriaQuery2.where(localPredicate2);
		Long localLong = (Long) this.entityManager.createQuery(
				localCriteriaQuery2).setFlushMode(FlushModeType.COMMIT)
				.getSingleResult();
		int i = (int) Math.ceil(localLong.longValue() / pageable.getPageSize());
		if (i < pageable.getPageNumber())
			pageable.setPageNumber(i);
		localCriteriaQuery1
				.orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
						.desc(localCriteriaBuilder.sum(localCriteriaBuilder
								.prod(localJoin1.get("quantity"), localJoin1
										.get("price")))) });
		TypedQuery localTypedQuery = this.entityManager.createQuery(
				localCriteriaQuery1).setFlushMode(FlushModeType.COMMIT);
		localTypedQuery.setFirstResult((pageable.getPageNumber() - 1)
				* pageable.getPageSize());
		localTypedQuery.setMaxResults(pageable.getPageSize());
		return new Page(localTypedQuery.getResultList(), localLong.longValue(),
				pageable);
	}

	public Long count(Member favoriteMember, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift,
			Boolean isOutOfStock, Boolean isStockAlert) {
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery localCriteriaQuery = localCriteriaBuilder
				.createQuery(Product.class);
		Root localRoot = localCriteriaQuery.from(Product.class);
		localCriteriaQuery.select(localRoot);
		Predicate localPredicate = localCriteriaBuilder.conjunction();
		if (favoriteMember != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot
							.join("favoriteMembers"), favoriteMember));
		if (isMarketable != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("isMarketable"),
							isMarketable));
		if (isList != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("isList"), isList));
		if (isTop != null)
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.equal(localRoot.get("isTop"), isTop));
		if (isGift != null)
			localPredicate = localCriteriaBuilder
					.and(localPredicate, localCriteriaBuilder.equal(localRoot
							.get("isGift"), isGift));
		Path localPath1 = localRoot.get("stock");
		Path localPath2 = localRoot.get("allocatedStock");
		if (isOutOfStock != null)
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
		if (isStockAlert != null) {
			Setting localSetting = SettingUtils.get();
			if (isStockAlert.booleanValue())
				localPredicate = localCriteriaBuilder.and(new Predicate[] {
						localPredicate,
						localCriteriaBuilder.isNotNull(localPath1),
						localCriteriaBuilder.lessThanOrEqualTo(localPath1,
								localCriteriaBuilder.sum(localPath2,
										localSetting.getStockAlertCount())) });
			else
				localPredicate = localCriteriaBuilder.and(localPredicate,
						localCriteriaBuilder.or(localCriteriaBuilder
								.isNull(localPath1), localCriteriaBuilder
								.greaterThan(localPath1, localCriteriaBuilder
										.sum(localPath2, localSetting
												.getStockAlertCount()))));
		}
		localCriteriaQuery.where(localPredicate);
		return super.count(localCriteriaQuery, null);
	}

	public boolean isPurchased(Member member, Product product) {
		if ((member == null) || (product == null))
			return false;
		String str = "select count(*) from OrderItem orderItem where orderItem.product = :product and orderItem.order.member = :member and orderItem.order.orderStatus = :orderStatus";
		Long localLong = (Long) this.entityManager.createQuery(str, Long.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("product",
						product).setParameter("member", member).setParameter(
						"orderStatus", OrderOrderStatus.completed)
				.getSingleResult();
		return localLong.longValue() > 0L;
	}

	public void persist(Product product) {
		Assert.notNull(product);
		IIIllIlI(product);
		super.persist(product);
	}

	public Product merge(Product product) {
		Assert.notNull(product);
		String str;
		if (!product.getIsGift().booleanValue()) {
			str = "delete from GiftItem giftItem where giftItem.gift = :product";
			this.entityManager.createQuery(str).setFlushMode(
					FlushModeType.COMMIT).setParameter("product", product)
					.executeUpdate();
		}
		if ((!product.getIsMarketable().booleanValue())
				|| (product.getIsGift().booleanValue())) {
			str = "delete from CartItem cartItem where cartItem.product = :product";
			this.entityManager.createQuery(str).setFlushMode(
					FlushModeType.COMMIT).setParameter("product", product)
					.executeUpdate();
		}
		IIIllIlI(product);
		return (Product) super.merge(product);
	}

	public void remove(Product product) {
		if (product != null) {
			Goods localGoods = product.getGoods();
			if ((localGoods != null) && (localGoods.getProducts() != null)) {
				localGoods.getProducts().remove(product);
				if (localGoods.getProducts().isEmpty())
					this.goodsDao.remove(localGoods);
			}
		}
		super.remove(product);
	}

	private void IIIllIlI(Product paramProduct) {
		if (paramProduct == null)
			return;
		if (StringUtils.isEmpty(paramProduct.getSn())) {
			String localObject;
			do {
				localObject = this.snDao.generate(SnType.product);
			} while (snExists(localObject));
			paramProduct.setSn(localObject);
		}
		StringBuffer localObject = new StringBuffer(paramProduct.getName());
		if ((paramProduct.getSpecificationValues() != null)
				&& (!paramProduct.getSpecificationValues().isEmpty())) {
			ArrayList localArrayList = new ArrayList(paramProduct
					.getSpecificationValues());
			Collections.sort(localArrayList, new ProductDaoImpl$1(this));
			localObject.append("[");
			int i = 0;
			Iterator localIterator = localArrayList.iterator();
			while (localIterator.hasNext()) {
				if (i != 0)
					localObject.append(" ");
				localObject.append(((SpecificationValue) localIterator.next())
						.getName());
				i++;
			}
			localObject.append("]");
		}
		paramProduct.setFullName(localObject.toString());
	}
}