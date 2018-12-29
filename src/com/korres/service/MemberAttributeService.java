package com.korres.service;

import java.util.List;
import com.korres.entity.MemberAttribute;

public abstract interface MemberAttributeService extends
		BaseService<MemberAttribute, Long> {
	public abstract Integer findUnusedPropertyIndex();

	public abstract List<MemberAttribute> findList();

	public abstract List<MemberAttribute> findList(String paramString);
}