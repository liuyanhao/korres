package com.korres.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.LockModeType;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

public abstract interface BaseDao<T, ID extends Serializable> {
	public abstract T find(ID paramID);

	public abstract List<T> findList(Integer paramInteger1,
			Integer paramInteger2, List<Filter> paramList,
			List<Order> paramList1);

	public abstract Page<T> findPage(Pageable paramPageable);

	public abstract long count(Filter[] paramArrayOfFilter);

	public abstract void persist(T paramT);

	public abstract T merge(T paramT);

	public abstract void remove(T paramT);

	public abstract void refresh(T paramT);

	public abstract ID getIdentifier(T paramT);

	public abstract boolean isManaged(T paramT);

	public abstract void detach(T paramT);

	public abstract void lock(T paramT, LockModeType paramLockModeType);

	public abstract void clear();

	public abstract void flush();
}