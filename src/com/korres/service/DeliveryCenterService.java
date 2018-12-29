package com.korres.service;

import com.korres.entity.DeliveryCenter;

public abstract interface DeliveryCenterService extends
		BaseService<DeliveryCenter, Long> {
	public abstract DeliveryCenter findDefault();
}