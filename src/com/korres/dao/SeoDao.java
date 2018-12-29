package com.korres.dao;

import com.korres.entity.Seo;
import com.korres.entity.Seo.SeoType;

public abstract interface SeoDao extends BaseDao<Seo, Long> {
	public abstract Seo find(SeoType paramType);
}