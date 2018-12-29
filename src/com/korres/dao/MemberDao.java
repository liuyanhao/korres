package com.korres.dao;

import java.util.Date;
import java.util.List;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Member;

public abstract interface MemberDao extends BaseDao<Member, Long> {
	public abstract boolean usernameExists(String paramString);

	public abstract boolean emailExists(String paramString);

	public abstract Member findByUsername(String paramString);

	public abstract List<Member> findListByEmail(String paramString);

	public abstract Page<Object> findPurchasePage(Date paramDate1,
			Date paramDate2, Pageable paramPageable);
}