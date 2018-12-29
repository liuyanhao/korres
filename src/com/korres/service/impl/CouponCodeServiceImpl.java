package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import javax.persistence.LockModeType;
import com.korres.dao.CouponCodeDao;
import com.korres.dao.MemberDao;
import com.korres.entity.Coupon;
import com.korres.entity.CouponCode;
import com.korres.entity.Member;
import com.korres.service.CouponCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.korres.Page;
import com.korres.Pageable;

@Service("couponCodeServiceImpl")
public class CouponCodeServiceImpl extends BaseServiceImpl<CouponCode, Long>
		implements CouponCodeService {

	@Resource(name = "couponCodeDaoImpl")
	private CouponCodeDao couponCodeDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "couponCodeDaoImpl")
	public void setBaseDao(CouponCodeDao couponCodeDao) {
		super.setBaseDao(couponCodeDao);
	}

	@Transactional(readOnly = true)
	public boolean codeExists(String code) {
		return this.couponCodeDao.codeExists(code);
	}

	@Transactional(readOnly = true)
	public CouponCode findByCode(String code) {
		return this.couponCodeDao.findByCode(code);
	}

	public CouponCode build(Coupon coupon, Member member) {
		return this.couponCodeDao.build(coupon, member);
	}

	public List<CouponCode> build(Coupon coupon, Member member, Integer count) {
		return this.couponCodeDao.build(coupon, member, count);
	}

	public CouponCode exchange(Coupon coupon, Member member) {
		Assert.notNull(coupon);
		Assert.notNull(member);
		this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
		member.setPoint(Long.valueOf(member.getPoint().longValue()
				- coupon.getPoint().intValue()));
		this.memberDao.merge(member);
		return this.couponCodeDao.build(coupon, member);
	}

	@Transactional(readOnly = true)
	public Page<CouponCode> findPage(Member member, Pageable pageable) {
		return this.couponCodeDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Coupon coupon, Member member, Boolean hasBegun,
			Boolean hasExpired, Boolean isUsed) {
		return this.couponCodeDao.count(coupon, member, hasBegun, hasExpired,
				isUsed);
	}
}