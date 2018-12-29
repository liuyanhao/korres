package com.korres.service;

import java.io.Serializable;
import java.util.List;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

public abstract interface BaseService<T, ID extends Serializable> {
	public abstract T find(ID paramID);

	public abstract List<T> findAll();

	public abstract List<T> findList(ID[] paramArrayOfID);

	public abstract List<T> findList(Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1);

	public abstract List<T> findList(Integer paramInteger1,
			Integer paramInteger2, List<Filter> paramList,
			List<Order> paramList1);

	public abstract Page<T> findPage(Pageable paramPageable);

	public abstract long count();

	public abstract long count(Filter[] paramArrayOfFilter);

	public abstract boolean exists(ID paramID);

	public abstract boolean exists(Filter[] paramArrayOfFilter);

	public abstract void save(T paramT);

	public abstract T update(T paramT);

	public abstract T update(T paramT, String[] paramArrayOfString);

	public abstract void delete(ID paramID);

	public abstract void delete(ID[] paramArrayOfID);

	public abstract void delete(T paramT);
}