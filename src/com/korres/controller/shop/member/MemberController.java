package com.korres.controller.shop.member;

import javax.annotation.Resource;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.service.ConsultationService;
import com.korres.service.CouponCodeService;
import com.korres.service.MemberService;
import com.korres.service.MessageService;
import com.korres.service.OrderService;
import com.korres.service.ProductNotifyService;
import com.korres.service.ProductService;
import com.korres.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("shopMemberController")
@RequestMapping( { "/member" })
public class MemberController extends BaseController {
	private static final int IIIlllIl = 6;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;

	@Resource(name = "messageServiceImpl")
	private MessageService messageService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productNotifyServiceImpl")
	private ProductNotifyService productNotifyService;

	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;

	@RequestMapping(value = { "/index" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		model.addAttribute("waitingPaymentOrderCount", this.orderService
				.waitingPaymentCount(member));
		model.addAttribute("waitingShippingOrderCount", this.orderService
				.waitingShippingCount(member));
		model.addAttribute("messageCount", this.messageService.count(member,
				Boolean.valueOf(false)));
		model.addAttribute("couponCodeCount", this.couponCodeService.count(
				null, member, null, Boolean.valueOf(false), Boolean
						.valueOf(false)));
		model.addAttribute("favoriteCount", this.productService.count(member,
				null, null, null, null, null, null));
		model.addAttribute("productNotifyCount", this.productNotifyService
				.count(member, null, null, null));
		model.addAttribute("reviewCount", this.reviewService.count(member,
				null, null, null));
		model.addAttribute("consultationCount", this.consultationService.count(
				member, null, null));
		model.addAttribute("newOrders", this.orderService.findList(member,
				Integer.valueOf(6), null, null));
		return "shop/member/index";
	}
}