package com.korres.service;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Coupon;

public abstract interface CouponService extends BaseService<Coupon, Long> {
	public abstract Page<Coupon> findPage(Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3, Pageable paramPageable);
}