package com.korres.service;

import java.util.Map;
import com.korres.entity.Shipping;

public abstract interface ShippingService extends BaseService<Shipping, Long> {
	public abstract Shipping findBySn(String paramString);

	public abstract Map<String, Object> query(Shipping paramShipping);
}