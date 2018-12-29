package com.korres.dao;

import java.util.List;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Coupon;
import com.korres.entity.CouponCode;
import com.korres.entity.Member;

public abstract interface CouponCodeDao extends BaseDao<CouponCode, Long> {
	public abstract boolean codeExists(String paramString);

	public abstract CouponCode findByCode(String paramString);

	public abstract CouponCode build(Coupon paramCoupon, Member paramMember);

	public abstract List<CouponCode> build(Coupon paramCoupon,
			Member paramMember, Integer paramInteger);

	public abstract Page<CouponCode> findPage(Member paramMember,
			Pageable paramPageable);

	public abstract Long count(Coupon paramCoupon, Member paramMember,
			Boolean paramBoolean1, Boolean paramBoolean2, Boolean paramBoolean3);
}