package com.korres.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Admin;
import com.korres.entity.Member;

public abstract interface MemberService extends BaseService<Member, Long> {
	public abstract boolean usernameExists(String paramString);

	public abstract boolean usernameDisabled(String paramString);

	public abstract boolean emailExists(String paramString);

	public abstract boolean emailUnique(String paramString1, String paramString2);

	public abstract void save(Member paramMember, Admin paramAdmin);

	public abstract void update(Member paramMember, Integer paramInteger,
			BigDecimal paramBigDecimal, String paramString, Admin paramAdmin);

	public abstract Member findByUsername(String paramString);

	public abstract List<Member> findListByEmail(String paramString);

	public abstract Page<Object> findPurchasePage(Date paramDate1,
			Date paramDate2, Pageable paramPageable);

	public abstract boolean isAuthenticated();

	public abstract Member getCurrent();

	public abstract String getCurrentUsername();
}