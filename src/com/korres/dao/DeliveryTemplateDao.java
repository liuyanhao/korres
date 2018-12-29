package com.korres.dao;

import com.korres.entity.DeliveryTemplate;

public abstract interface DeliveryTemplateDao extends
		BaseDao<DeliveryTemplate, Long> {
	public abstract DeliveryTemplate findDefault();
}