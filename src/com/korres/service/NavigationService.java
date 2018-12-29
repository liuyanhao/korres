package com.korres.service;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;

import com.korres.entity.Navigation;
import com.korres.entity.Navigation.NavigationPosition;

public abstract interface NavigationService extends
		BaseService<Navigation, Long> {
	public abstract List<Navigation> findList(NavigationPosition paramPosition);

	public abstract List<Navigation> findList(Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1, String paramString);
}