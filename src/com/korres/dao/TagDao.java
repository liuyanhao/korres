package com.korres.dao;

import java.util.List;

import com.korres.entity.Tag;
import com.korres.entity.Tag.TagType;

public abstract interface TagDao extends BaseDao<Tag, Long> {
	public abstract List<Tag> findList(TagType paramType);
}