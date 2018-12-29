package com.korres.service.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.korres.dao.BaseDao;
import com.korres.service.BaseService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

@Transactional
public class BaseServiceImpl<T, ID extends Serializable> implements
		BaseService<T, ID> {
	private static final String[] IIIllIlI = { "id", "createDate", "modifyDate" };
	private BaseDao<T, ID> baseDao;

	public void setBaseDao(BaseDao<T, ID> baseDao) {
		this.baseDao = baseDao;
	}

	@Transactional(readOnly = true)
	public T find(ID id) {
		return this.baseDao.find(id);
	}

	@Transactional(readOnly = true)
	public List<T> findAll() {
		return findList(null, null, null, null);
	}

	@Transactional(readOnly = true)
	public List<T> findList(ID[] ids) {
		ArrayList localArrayList = new ArrayList();
		if (ids != null)
			for (Serializable localSerializable : ids) {
				Object localObject = find((ID) localSerializable);
				if (localObject != null)
					localArrayList.add(localObject);
			}
		return localArrayList;
	}

	@Transactional(readOnly = true)
	public List<T> findList(Integer count, List<Filter> filters,
			List<Order> orders) {
		return findList(null, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<T> findList(Integer first, Integer count, List<Filter> filters,
			List<Order> orders) {
		return this.baseDao.findList(first, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<T> findPage(Pageable pageable) {
		return this.baseDao.findPage(pageable);
	}

	@Transactional(readOnly = true)
	public long count() {
		return count(new Filter[0]);
	}

	@Transactional(readOnly = true)
	public long count(Filter[] filters) {
		return this.baseDao.count(filters);
	}

	@Transactional(readOnly = true)
	public boolean exists(ID id) {
		return this.baseDao.find(id) != null;
	}

	@Transactional(readOnly = true)
	public boolean exists(Filter[] filters) {
		return this.baseDao.count(filters) > 0L;
	}

	@Transactional
	public void save(T entity) {
		this.baseDao.persist(entity);
	}

	@Transactional
	public T update(T entity) {
		return this.baseDao.merge(entity);
	}

	@Transactional
	public T update(T entity, String[] ignoreProperties) {
		Assert.notNull(entity);
		if (this.baseDao.isManaged(entity))
			throw new IllegalArgumentException("Entity must not be managed");
		Object localObject = this.baseDao.find(this.baseDao
				.getIdentifier(entity));
		if (localObject != null) {
			IIIllIlI(entity, localObject, (String[]) ArrayUtils.addAll(
					ignoreProperties, IIIllIlI));
			return update((T) localObject);
		}
		return update(entity);
	}

	@Transactional
	public void delete(ID id) {
		delete(this.baseDao.find(id));
	}

	@Transactional
	public void delete(ID[] ids) {
		if (ids != null)
			for (Serializable localSerializable : ids)
				delete(this.baseDao.find((ID) localSerializable));
	}

	@Transactional
	public void delete(T entity) {
		this.baseDao.remove(entity);
	}

	private void IIIllIlI(Object paramObject1, Object paramObject2,
			String[] paramArrayOfString) {
		Assert.notNull(paramObject1, "Source must not be null");
		Assert.notNull(paramObject2, "Target must not be null");
		PropertyDescriptor[] lipd = BeanUtils
				.getPropertyDescriptors(paramObject2.getClass());
		List<String> localObject1 = paramArrayOfString != null ? Arrays
				.asList(paramArrayOfString) : null;
		for (PropertyDescriptor pd : lipd)
			if ((pd.getWriteMethod() != null)
					&& ((paramArrayOfString == null) || (!localObject1
							.contains(pd.getName())))) {
				PropertyDescriptor propertyDescriptor = BeanUtils
						.getPropertyDescriptor(paramObject1.getClass(), pd
								.getName());
				if ((propertyDescriptor != null)
						&& (propertyDescriptor.getReadMethod() != null)) {
					try {
						Method method = propertyDescriptor.getReadMethod();
						if (!Modifier.isPublic(method.getDeclaringClass()
								.getModifiers())) {
							method.setAccessible(true);
						}

						Object localObject2 = method.invoke(paramObject1,
								new Object[0]);
						Object localObject3 = method.invoke(paramObject2,
								new Object[0]);
						// Object localObject4;
						if ((localObject2 != null) && (localObject3 != null)
								&& ((localObject3 instanceof Collection))) {
							Collection collection = (Collection) localObject3;
							collection.clear();
							collection.addAll((Collection) localObject2);
						} else {
							Method m = pd.getWriteMethod();
							if (!Modifier.isPublic(m.getDeclaringClass()
									.getModifiers())) {
								m.setAccessible(true);
							}
							m.invoke(paramObject2,
									new Object[] { localObject2 });
						}
					} catch (Throwable throwable) {
						throw new FatalBeanException(
								"Could not copy properties from source to target",
								throwable);
					}
				}
			}
	}
}