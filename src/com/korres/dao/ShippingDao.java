package com.korres.dao;

import com.korres.entity.Shipping;

public abstract interface ShippingDao extends BaseDao<Shipping, Long> {
	public abstract Shipping findBySn(String paramString);
}