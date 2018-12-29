package com.korres.dao;

import java.util.List;
import com.korres.entity.MemberAttribute;

public abstract interface MemberAttributeDao extends
		BaseDao<MemberAttribute, Long> {
	public abstract Integer findUnusedPropertyIndex();

	public abstract List<MemberAttribute> findList();
}