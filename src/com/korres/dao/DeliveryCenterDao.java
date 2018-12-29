package com.korres.dao;

import com.korres.entity.DeliveryCenter;

public abstract interface DeliveryCenterDao extends
		BaseDao<DeliveryCenter, Long> {
	public abstract DeliveryCenter findDefault();
}