package com.korres.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.korres.dao.BaseDao;
import com.korres.entity.OrderEntity;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;
import com.korres.Filter.FilterOperator;
import com.korres.Order.OrderDirection;

public abstract class BaseDaoImpl<T, ID extends Serializable> implements
		BaseDao<T, ID> {
	private Class<T> clazz;
	private static volatile long ALIAS_NUM = 0L;

	@PersistenceContext
	protected EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		Type type = getClass().getGenericSuperclass();
		Type[] arrayOfType = ((ParameterizedType) type).getActualTypeArguments();
		this.clazz = ((Class) arrayOfType[0]);
	}

	//获取Entity通过主键
	public T find(ID id) {
		if (id != null){
			return this.entityManager.find(this.clazz, id);
		}
		
		return null;
	}

	//获取Entity列表
	@SuppressWarnings("unchecked")
	public List<T> findList(Integer first, Integer count, List<com.korres.Filter> filters,
			List<com.korres.Order> orders) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(this.clazz);
		criteriaQuery.select(criteriaQuery.from(this.clazz));
		return findList(criteriaQuery, first, count, filters, orders);
	}

	//获取Entity通过分页
	@SuppressWarnings("unchecked")
	public Page<T> findPage(Pageable pageable) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(this.clazz);
		criteriaQuery.select(criteriaQuery.from(this.clazz));
		return findPage(criteriaQuery, pageable);
	}

	//获取个数
	@SuppressWarnings("unchecked")
	public long count(Filter[] filters) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(this.clazz);
		criteriaQuery.select(criteriaQuery.from(this.clazz));
		return count(criteriaQuery,	filters != null ? Arrays.asList(filters) : null).longValue();
	}

	//添加 Entity Bean
	public void persist(T entity) {
		Assert.notNull(entity);
		this.entityManager.persist(entity);
	}

	//
	public T merge(T entity) {
		Assert.notNull(entity);
		return this.entityManager.merge(entity);
	}

	//删除Entity
	public void remove(T entity) {
		if (entity != null)
			this.entityManager.remove(entity);
	}

	//刷新实体Entity	Bean
	public void refresh(T entity) {
		Assert.notNull(entity);
		this.entityManager.refresh(entity);
	}

	@SuppressWarnings("unchecked")
	public ID getIdentifier(T entity) {
		Assert.notNull(entity);
		return (ID) this.entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
	}

	//检测实体当前是否被管理中
	public boolean isManaged(T entity) {
		return this.entityManager.contains(entity);
	}

	public void detach(T entity) {
		this.entityManager.detach(entity);
	}

	//锁定Entity
	public void lock(T entity, LockModeType lockModeType) {
		if ((entity != null) && (lockModeType != null))
			this.entityManager.lock(entity, lockModeType);
	}

	//分离所有当前正在被管理的实体
	public void clear() {
		this.entityManager.clear();
	}

	//将实体的改变立刻刷新到数据库中
	public void flush() {
		this.entityManager.flush();
	}

	@SuppressWarnings("unchecked")
	protected List<T> findList(CriteriaQuery<T> criteriaQuery,
			Integer first, Integer count,
			List<Filter> filters, List<com.korres.Order> orders) {
		Assert.notNull(criteriaQuery);
		Assert.notNull(criteriaQuery.getSelection());
		Assert.notEmpty(criteriaQuery.getRoots());
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		Root root = getRoot(criteriaQuery);
		where(criteriaQuery, filters);
		orderBy(criteriaQuery, orders);
		if (criteriaQuery.getOrderList().isEmpty())
			if (OrderEntity.class.isAssignableFrom(this.clazz)){
				criteriaQuery.orderBy(new javax.persistence.criteria.Order[] { criteriaBuilder.asc(root.get("order")) });
			}
			else{
				criteriaQuery.orderBy(new javax.persistence.criteria.Order[] { criteriaBuilder.desc(root.get("createDate")) });
			}
		
		TypedQuery typedQuery = this.entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
		if (first != null){
			typedQuery.setFirstResult(first.intValue());
		}
		if (count != null){
			typedQuery.setMaxResults(count.intValue());
		}
		
		return typedQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	protected Page<T> findPage(CriteriaQuery<T> criteriaQuery,
			Pageable pageable) {
		Assert.notNull(criteriaQuery);
		Assert.notNull(criteriaQuery.getSelection());
		Assert.notEmpty(criteriaQuery.getRoots());
		if (pageable == null)
			pageable = new Pageable();
		CriteriaBuilder localCriteriaBuilder = this.entityManager.getCriteriaBuilder();
		Root localRoot = getRoot(criteriaQuery);
		where(criteriaQuery, pageable);
		orderBy(criteriaQuery, pageable);
		if (criteriaQuery.getOrderList().isEmpty()){
			if (OrderEntity.class.isAssignableFrom(this.clazz)){
				criteriaQuery.orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
								.asc(localRoot.get("order")) });
			}
			else {
				criteriaQuery.orderBy(new javax.persistence.criteria.Order[] { localCriteriaBuilder
								.desc(localRoot.get("createDate")) });
			}
		}
		long total = count(criteriaQuery, null).longValue();
		int i = (int) Math.ceil(total / pageable.getPageSize());
		if (i < pageable.getPageNumber()){
			pageable.setPageNumber(i);
		}
		TypedQuery typedQuery = this.entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
		typedQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
		typedQuery.setMaxResults(pageable.getPageSize());
		return new Page(typedQuery.getResultList(), total, pageable);
	}

	@SuppressWarnings("unchecked")
	protected Long count(CriteriaQuery<T> paramCriteriaQuery, List<Filter> paramList) {
		Assert.notNull(paramCriteriaQuery);
		Assert.notNull(paramCriteriaQuery.getSelection());
		Assert.notEmpty(paramCriteriaQuery.getRoots());
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		where(paramCriteriaQuery, paramList);
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Iterator iterator = paramCriteriaQuery.getRoots().iterator();
		while (iterator.hasNext()) {
			Root localRoot1 = (Root) iterator.next();
			Root localRoot2 = criteriaQuery.from(localRoot1.getJavaType());
			localRoot2.alias(selection(localRoot1));
			from(localRoot1, localRoot2);
		}
		
		Root root = getRoot(criteriaQuery, paramCriteriaQuery.getResultType());
		criteriaQuery.select(criteriaBuilder.count(root));
		if (paramCriteriaQuery.getGroupList() != null){
			criteriaQuery.groupBy(paramCriteriaQuery.getGroupList());
		}
		if (paramCriteriaQuery.getGroupRestriction() != null){
			criteriaQuery.having(paramCriteriaQuery.getGroupRestriction());
		}
		if (paramCriteriaQuery.getRestriction() != null){
			criteriaQuery.where(paramCriteriaQuery.getRestriction());
		}
		
		return (Long) this.entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	private synchronized String selection(Selection<?> paramSelection) {
		if (paramSelection != null) {
			String str = paramSelection.getAlias();
			if (str == null) {
				if (ALIAS_NUM >= 1000L)
					ALIAS_NUM = 0L;
				str = "shopxxGeneratedAlias" + ALIAS_NUM++;
				paramSelection.alias(str);
			}
			return str;
		}
		return null;
	}

	private Root<T> getRoot(CriteriaQuery<T> criteriaQuery) {
		if (criteriaQuery != null)
			return getRoot(criteriaQuery, criteriaQuery.getResultType());
		return null;
	}

	@SuppressWarnings("unchecked")
	private Root<T> getRoot(CriteriaQuery<?> criteriaQuery, Class<T> clazz) {
		if ((criteriaQuery != null)
				&& (criteriaQuery.getRoots() != null)
				&& (clazz != null)) {
			Iterator iterator = criteriaQuery.getRoots().iterator();
			while (iterator.hasNext()) {
				Root root = (Root) iterator.next();
				if (clazz.equals(root.getJavaType()))
					return (Root) root.as(clazz);
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private void from(From<?, ?> paramFrom1, From<?, ?> paramFrom2) {
		Iterator iterator1 = paramFrom1.getJoins().iterator();
		while (iterator1.hasNext()) {
			Join join1 = (Join) iterator1.next();
			Join Join2 = paramFrom2.join(join1.getAttribute().getName(), join1.getJoinType());
			Join2.alias(selection(join1));
			from(join1, Join2);
		}
		
		Iterator iterator2 = paramFrom1.getFetches().iterator();
		while (iterator2.hasNext()) {
			Fetch fetch1 = (Fetch) iterator2.next();
			Fetch fetch2 = paramFrom2.fetch(fetch1.getAttribute().getName());
			fetch(fetch1, fetch2);
		}
	}

	@SuppressWarnings("unchecked")
	private void fetch(Fetch<?, ?> paramFetch1, Fetch<?, ?> paramFetch2) {
		Iterator iterator = paramFetch1.getFetches().iterator();
		while (iterator.hasNext()) {
			Fetch fetch1 = (Fetch) iterator.next();
			Fetch fetch2 = paramFetch2.fetch(fetch1.getAttribute().getName());
			fetch(fetch1, fetch2);
		}
	}

	private void where(CriteriaQuery<T> paramCriteriaQuery, List<Filter> paramList) {
		if ((paramCriteriaQuery == null) || (paramList == null)
				|| (paramList.isEmpty())){
			return;
		}
		
		Root root = getRoot(paramCriteriaQuery);
		if (root == null){
			return;
		}
		
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		Predicate localPredicate = paramCriteriaQuery.getRestriction() != null ? paramCriteriaQuery.getRestriction() : criteriaBuilder.conjunction();
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			Filter localFilter = (Filter) localIterator.next();
			if ((localFilter != null)
					&& (!StringUtils.isEmpty(localFilter.getProperty())))
				if ((localFilter.getOperator() == FilterOperator.eq)
						&& (localFilter.getValue() != null)) {
					if ((localFilter.getIgnoreCase() != null)
							&& (localFilter.getIgnoreCase().booleanValue())
							&& ((localFilter.getValue() instanceof String)))
						localPredicate = criteriaBuilder.and(
								localPredicate,
								criteriaBuilder.equal(criteriaBuilder.lower(root.get(localFilter.getProperty())), ((String) localFilter.getValue()).toLowerCase()));
					else
						localPredicate = criteriaBuilder.and(
								localPredicate, criteriaBuilder
										.equal(root.get(localFilter
												.getProperty()), localFilter
												.getValue()));
				} else if ((localFilter.getOperator() == FilterOperator.ne)
						&& (localFilter.getValue() != null)) {
					if ((localFilter.getIgnoreCase() != null)
							&& (localFilter.getIgnoreCase().booleanValue())
							&& ((localFilter.getValue() instanceof String)))
						localPredicate = criteriaBuilder
								.and(
										localPredicate,
										criteriaBuilder
												.notEqual(
														criteriaBuilder
																.lower(root
																		.get(localFilter
																				.getProperty())),
														((String) localFilter
																.getValue())
																.toLowerCase()));
					else
						localPredicate = criteriaBuilder.and(
								localPredicate, criteriaBuilder
										.notEqual(root.get(localFilter
												.getProperty()), localFilter
												.getValue()));
				} else if ((localFilter.getOperator() == FilterOperator.gt)
						&& (localFilter.getValue() != null))
					localPredicate = criteriaBuilder.and(localPredicate,
							criteriaBuilder.gt(root.get(localFilter
									.getProperty()), (Number) localFilter
									.getValue()));
				else if ((localFilter.getOperator() == FilterOperator.lt)
						&& (localFilter.getValue() != null))
					localPredicate = criteriaBuilder.and(localPredicate,
							criteriaBuilder.lt(root.get(localFilter
									.getProperty()), (Number) localFilter
									.getValue()));
				else if ((localFilter.getOperator() == FilterOperator.ge)
						&& (localFilter.getValue() != null)){
					localPredicate = criteriaBuilder.and(localPredicate,
							criteriaBuilder.ge(root.get(localFilter
									.getProperty()), (Number) localFilter
									.getValue()));
				}
				else if ((localFilter.getOperator() == FilterOperator.le)
						&& (localFilter.getValue() != null)){
					localPredicate = criteriaBuilder.and(localPredicate,
							criteriaBuilder.le(root.get(localFilter
									.getProperty()), (Number) localFilter
									.getValue()));
				}
				else if ((localFilter.getOperator() == FilterOperator.like)
						&& (localFilter.getValue() != null)
						&& ((localFilter.getValue() instanceof String))){
					localPredicate = criteriaBuilder.and(localPredicate,
							criteriaBuilder.like(root.get(localFilter.getProperty()), (String) localFilter.getValue()));
				}
				else if ((localFilter.getOperator() == FilterOperator.in)
						&& (localFilter.getValue() != null))
					localPredicate = criteriaBuilder.and(localPredicate,
							root.get(localFilter.getProperty()).in(new Object[] { localFilter.getValue() }));
				else if (localFilter.getOperator() == FilterOperator.isNull){
					localPredicate = criteriaBuilder.and(localPredicate, root.get(localFilter.getProperty()).isNull());
				}
				else if (localFilter.getOperator() == FilterOperator.isNotNull){
					localPredicate = criteriaBuilder.and(localPredicate,root.get(localFilter.getProperty()).isNotNull());
				}
		}
		
		paramCriteriaQuery.where(localPredicate);
	}

	private void where(CriteriaQuery<T> paramCriteriaQuery,
			Pageable paramPageable) {
		if ((paramCriteriaQuery == null) || (paramPageable == null)){
			return;
		}
		Root localRoot = getRoot(paramCriteriaQuery);
		if (localRoot == null)
			return;
		CriteriaBuilder localCriteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		Predicate localPredicate = paramCriteriaQuery.getRestriction() != null ? paramCriteriaQuery
				.getRestriction()
				: localCriteriaBuilder.conjunction();
		if ((StringUtils.isNotEmpty(paramPageable.getSearchProperty()))
				&& (StringUtils.isNotEmpty(paramPageable.getSearchValue())))
			localPredicate = localCriteriaBuilder.and(localPredicate,
					localCriteriaBuilder.like(localRoot.get(paramPageable
							.getSearchProperty()), "%"
							+ paramPageable.getSearchValue() + "%"));
		if (paramPageable.getFilters() != null) {
			Iterator localIterator = paramPageable.getFilters().iterator();
			while (localIterator.hasNext()) {
				Filter localFilter = (Filter) localIterator.next();
				if ((localFilter != null)
						&& (!StringUtils.isEmpty(localFilter.getProperty())))
					if ((localFilter.getOperator() == FilterOperator.eq)
							&& (localFilter.getValue() != null)) {
						if ((localFilter.getIgnoreCase() != null)
								&& (localFilter.getIgnoreCase().booleanValue())
								&& ((localFilter.getValue() instanceof String)))
							localPredicate = localCriteriaBuilder
									.and(
											localPredicate,
											localCriteriaBuilder
													.equal(
															localCriteriaBuilder
																	.lower(localRoot
																			.get(localFilter
																					.getProperty())),
															((String) localFilter
																	.getValue())
																	.toLowerCase()));
						else
							localPredicate = localCriteriaBuilder.and(
									localPredicate, localCriteriaBuilder.equal(
											localRoot.get(localFilter
													.getProperty()),
											localFilter.getValue()));
					} else if ((localFilter.getOperator() == FilterOperator.ne)
							&& (localFilter.getValue() != null)) {
						if ((localFilter.getIgnoreCase() != null)
								&& (localFilter.getIgnoreCase().booleanValue())
								&& ((localFilter.getValue() instanceof String)))
							localPredicate = localCriteriaBuilder
									.and(
											localPredicate,
											localCriteriaBuilder
													.notEqual(
															localCriteriaBuilder
																	.lower(localRoot
																			.get(localFilter
																					.getProperty())),
															((String) localFilter
																	.getValue())
																	.toLowerCase()));
						else
							localPredicate = localCriteriaBuilder.and(
									localPredicate, localCriteriaBuilder
											.notEqual(localRoot.get(localFilter
													.getProperty()),
													localFilter.getValue()));
					} else if ((localFilter.getOperator() == FilterOperator.gt)
							&& (localFilter.getValue() != null))
						localPredicate = localCriteriaBuilder.and(
								localPredicate,
								localCriteriaBuilder.gt(localRoot
										.get(localFilter.getProperty()),
										(Number) localFilter.getValue()));
					else if ((localFilter.getOperator() == FilterOperator.lt)
							&& (localFilter.getValue() != null))
						localPredicate = localCriteriaBuilder.and(
								localPredicate,
								localCriteriaBuilder.lt(localRoot
										.get(localFilter.getProperty()),
										(Number) localFilter.getValue()));
					else if ((localFilter.getOperator() == FilterOperator.ge)
							&& (localFilter.getValue() != null))
						localPredicate = localCriteriaBuilder.and(
								localPredicate,
								localCriteriaBuilder.ge(localRoot
										.get(localFilter.getProperty()),
										(Number) localFilter.getValue()));
					else if ((localFilter.getOperator() == FilterOperator.le)
							&& (localFilter.getValue() != null))
						localPredicate = localCriteriaBuilder.and(
								localPredicate,
								localCriteriaBuilder.le(localRoot
										.get(localFilter.getProperty()),
										(Number) localFilter.getValue()));
					else if ((localFilter.getOperator() == FilterOperator.like)
							&& (localFilter.getValue() != null)
							&& ((localFilter.getValue() instanceof String)))
						localPredicate = localCriteriaBuilder.and(
								localPredicate,
								localCriteriaBuilder.like(localRoot
										.get(localFilter.getProperty()),
										(String) localFilter.getValue()));
					else if ((localFilter.getOperator() == FilterOperator.in)
							&& (localFilter.getValue() != null))
						localPredicate = localCriteriaBuilder.and(
								localPredicate, localRoot.get(
										localFilter.getProperty())
										.in(
												new Object[] { localFilter
														.getValue() }));
					else if (localFilter.getOperator() == FilterOperator.isNull)
						localPredicate = localCriteriaBuilder.and(
								localPredicate, localRoot.get(
										localFilter.getProperty()).isNull());
					else if (localFilter.getOperator() == FilterOperator.isNotNull)
						localPredicate = localCriteriaBuilder.and(
								localPredicate, localRoot.get(
										localFilter.getProperty()).isNotNull());
			}
		}
		
		paramCriteriaQuery.where(localPredicate);
	}

	@SuppressWarnings("unchecked")
	private void orderBy(CriteriaQuery<T> paramCriteriaQuery, List<com.korres.Order> paramList) {
		if ((paramCriteriaQuery == null) || (paramList == null)
				|| (paramList.isEmpty())){
			return;
		}
		
		Root root = getRoot(paramCriteriaQuery);
		if (root == null){
			return;
		}
		CriteriaBuilder localCriteriaBuilder = this.entityManager.getCriteriaBuilder();
		List list = new ArrayList();
		if (!paramCriteriaQuery.getOrderList().isEmpty()){
			list.addAll(paramCriteriaQuery.getOrderList());
		}
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			com.korres.Order order = (com.korres.Order) localIterator.next();
			if (order.getDirection() == OrderDirection.asc){
				list.add(localCriteriaBuilder.asc(root.get(order.getProperty())));
			}
			else if (order.getDirection() == OrderDirection.desc){
				list.add(localCriteriaBuilder.desc(root.get(order.getProperty())));
			}
		}
		
		paramCriteriaQuery.orderBy(list);
	}

	@SuppressWarnings("unchecked")
	private void orderBy(CriteriaQuery<T> paramCriteriaQuery, Pageable paramPageable) {
		if ((paramCriteriaQuery == null) || (paramPageable == null)) {
			return;
		}

		Root root = getRoot(paramCriteriaQuery);
		if (root == null) {
			return;
		}
		
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		List list = new ArrayList();
		if (!paramCriteriaQuery.getOrderList().isEmpty()){
			list.addAll(paramCriteriaQuery.getOrderList());
		}
		
		if ((StringUtils.isNotEmpty(paramPageable.getOrderProperty())) && (paramPageable.getOrderDirection() != null)){
			if (paramPageable.getOrderDirection() == OrderDirection.asc){
				list.add(criteriaBuilder.asc(root.get(paramPageable.getOrderProperty())));
			}
			else if (paramPageable.getOrderDirection() == OrderDirection.desc){
				list.add(criteriaBuilder.desc(root.get(paramPageable.getOrderProperty())));
			}
		}
		
		if (paramPageable.getOrders() != null) {
			Iterator iterator = paramPageable.getOrders().iterator();
			while (iterator.hasNext()) {
				com.korres.Order order = (com.korres.Order) iterator.next();
				if (order.getDirection() == OrderDirection.asc){
					list.add(criteriaBuilder.asc(root.get(order.getProperty())));
				}
				else if (order.getDirection() == OrderDirection.desc){
					list.add(criteriaBuilder.desc(root.get(order.getProperty())));
				}
			}
		}
		
		paramCriteriaQuery.orderBy(list);
	}
}