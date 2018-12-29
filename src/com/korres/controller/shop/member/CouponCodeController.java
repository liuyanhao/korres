package com.korres.controller.shop.member;

import javax.annotation.Resource;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Coupon;
import com.korres.entity.Member;
import com.korres.service.CouponCodeService;
import com.korres.service.CouponService;
import com.korres.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

/*
 * 类名：CouponCodeController.java
 * 功能说明：优惠券
 * 创建日期：2018-12-20 下午04:45:16
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Controller("shopMemberCouponCodeController")
@RequestMapping( { "/member/coupon_code" })
public class CouponCodeController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "couponServiceImpl")
	private CouponService couponService;

	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;

	/**
	 * 兑换优惠券
	 * @param pageNumber
	 * @param model
	 * @return
	 * @author weiyuanhua
	 * @date 2018-12-20 下午04:48:39
	 */
	@RequestMapping(value = { "/exchange" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String exchange(Integer pageNumber, ModelMap model) {
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.couponService.findPage(Boolean
				.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(false),
				pageable));

		return "shop/member/coupon_code/exchange";
	}

	/**
	 * 兑换优惠券
	 * @param id
	 * @return
	 * @author weiyuanhua
	 * @date 2018-12-20 下午04:48:47
	 */
	@RequestMapping(value = { "/exchange" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message exchange(Long id) {
		Coupon coupon = this.couponService.find(id);
		if ((coupon == null) || (!coupon.getIsEnabled().booleanValue())
				|| (!coupon.getIsExchange().booleanValue())
				|| (coupon.hasExpired())) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (member.getPoint().longValue() < coupon.getPoint().intValue()) {
			return Message.warn("shop.member.couponCode.point", new Object[0]);
		}

		this.couponCodeService.exchange(coupon, member);

		return SHOP_MESSAGE_SUCCESS;
	}

	/**
	 * 优惠券列表
	 * @param pageNumber
	 * @param model
	 * @return
	 * @author liuxicai
	 * @date 2018-12-20 下午04:45:53
	 */
	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.couponCodeService.findPage(member,
				pageable));

		return "shop/member/coupon_code/list";
	}
}