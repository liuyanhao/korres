package com.korres.service;

import com.korres.entity.DeliveryTemplate;

public abstract interface DeliveryTemplateService extends
		BaseService<DeliveryTemplate, Long> {
	public abstract DeliveryTemplate findDefault();
}