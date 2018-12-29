package com.korres.service;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;

import com.korres.entity.Promotion;

public abstract interface PromotionService extends BaseService<Promotion, Long> {
	public abstract List<Promotion> findList(Boolean paramBoolean1,
			Boolean paramBoolean2, Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1);

	public abstract List<Promotion> findList(Boolean paramBoolean1,
			Boolean paramBoolean2, Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1, String paramString);
}