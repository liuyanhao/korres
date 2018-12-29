package com.korres.service;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;

import com.korres.entity.Brand;

public abstract interface BrandService extends BaseService<Brand, Long> {
	public abstract List<Brand> findList(Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1, String paramString);
}