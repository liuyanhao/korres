package com.korres.service;

import com.korres.entity.Seo;
import com.korres.entity.Seo.SeoType;

public abstract interface SeoService extends BaseService<Seo, Long> {
	public abstract Seo find(SeoType paramType);

	public abstract Seo find(SeoType paramType, String paramString);
}