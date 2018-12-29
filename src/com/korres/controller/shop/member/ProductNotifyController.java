package com.korres.controller.shop.member;

import javax.annotation.Resource;

import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.entity.ProductNotify;
import com.korres.service.MemberService;
import com.korres.service.ProductNotifyService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("shopMemberProductNotifyController")
@RequestMapping( { "/member/product_notify" })
public class ProductNotifyController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "productNotifyServiceImpl")
	private ProductNotifyService productNotifyService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, Model model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.productNotifyService.findPage(member,
				null, null, null, pageable));

		return "/shop/member/product_notify/list";
	}

	@RequestMapping( { "delete" })
	@ResponseBody
	public Message delete(Long id) {
		ProductNotify productNotify = (ProductNotify) this.productNotifyService
				.find(id);
		if (productNotify == null) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (!member.getProductNotifies().contains(productNotify)) {
			return SHOP_MESSAGE_ERROR;
		}

		this.productNotifyService.delete(productNotify);

		return SHOP_MESSAGE_SUCCESS;
	}
}