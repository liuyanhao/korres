package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.CouponDao;
import com.korres.entity.Coupon;
import com.korres.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Page;
import com.korres.Pageable;

@Service("couponServiceImpl")
public class CouponServiceImpl extends BaseServiceImpl<Coupon, Long> implements
		CouponService {

	@Resource(name = "couponDaoImpl")
	private CouponDao couponDao;

	@Resource(name = "couponDaoImpl")
	public void setBaseDao(CouponDao couponDao) {
		super.setBaseDao(couponDao);
	}

	@Transactional(readOnly = true)
	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange,
			Boolean hasExpired, Pageable pageable) {
		return this.couponDao.findPage(isEnabled, isExchange, hasExpired,
				pageable);
	}
}