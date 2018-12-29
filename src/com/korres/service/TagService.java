package com.korres.service;

import java.util.List;

import com.korres.Filter;
import com.korres.Order;

import com.korres.entity.Tag;
import com.korres.entity.Tag.TagType;

public abstract interface TagService extends BaseService<Tag, Long> {
	public abstract List<Tag> findList(TagType paramType);

	public abstract List<Tag> findList(Integer paramInteger,
			List<Filter> paramList, List<Order> paramList1, String paramString);
}