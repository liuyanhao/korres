package com.korres.controller.shop.member;

import javax.annotation.Resource;

import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.service.MemberService;
import com.korres.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("shopMemberFavoriteController")
@RequestMapping( { "/member/favorite" })
public class FavoriteController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message add(Long id) {
		Product product = this.productService.find(id);
		if (product == null) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (member.getFavoriteProducts().contains(product)) {
			return Message.warn("shop.member.favorite.exist", new Object[0]);
		}

		if ((Member.MAX_FAVORITE_COUNT != null)
				&& (member.getFavoriteProducts().size() >= Member.MAX_FAVORITE_COUNT
						.intValue())) {
			return Message.warn("shop.member.favorite.addCountNotAllowed",
					new Object[] { Member.MAX_FAVORITE_COUNT });
		}

		member.getFavoriteProducts().add(product);
		this.memberService.update(member);

		return Message.success("shop.member.favorite.success", new Object[0]);
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.productService.findPage(member,
				pageable));

		return "shop/member/favorite/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long id) {
		Product product = (Product) this.productService.find(id);
		if (product == null) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (!member.getFavoriteProducts().contains(product)) {
			return SHOP_MESSAGE_ERROR;
		}

		member.getFavoriteProducts().remove(product);
		this.memberService.update(member);

		return SHOP_MESSAGE_SUCCESS;
	}
}