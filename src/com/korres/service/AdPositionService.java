package com.korres.service;

import com.korres.entity.AdPosition;

public abstract interface AdPositionService extends
		BaseService<AdPosition, Long> {
	public abstract AdPosition find(Long paramLong, String paramString);
}