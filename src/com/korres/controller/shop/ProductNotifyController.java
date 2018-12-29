package com.korres.controller.shop;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.ProductNotify;
import com.korres.service.MemberService;
import com.korres.service.ProductNotifyService;
import com.korres.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;

@Controller("shopProductNotifyController")
@RequestMapping({ "/product_notify" })
public class ProductNotifyController extends BaseController {

	@Resource(name = "productNotifyServiceImpl")
	private ProductNotifyService productNotifyService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@RequestMapping(value = { "/email" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<String, String> email() {
		Member member = this.memberService.getCurrent();
		String email = member != null ? member.getEmail() : null;
		Map<String, String> map = new HashMap<String, String>();
		map.put("email", email);

		return map;
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> save(String email, Long productId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!validate(ProductNotify.class, "email", email, new Class[0])) {
			map.put("message", SHOP_MESSAGE_ERROR);

			return map;
		}

		Product product = (Product) this.productService.find(productId);
		if (product == null) {
			map.put("message", Message.warn(
					"shop.productNotify.productNotExist", new Object[0]));

			return map;
		}

		if (!product.getIsMarketable().booleanValue()) {
			map.put("message", Message.warn(
					"shop.productNotify.productNotMarketable", new Object[0]));

			return map;
		}

		if (!product.getIsOutOfStock().booleanValue()) {
			map.put("message", Message.warn(
					"shop.productNotify.productInStock", new Object[0]));
		}

		if (this.productNotifyService.exists(product, email)) {
			map.put("message",
					Message.warn("shop.productNotify.exist", new Object[0]));
		} else {
			ProductNotify productNotify = new ProductNotify();
			productNotify.setEmail(email);
			productNotify.setHasSent(Boolean.valueOf(false));
			productNotify.setMember(this.memberService.getCurrent());
			productNotify.setProduct(product);
			this.productNotifyService.save(productNotify);
			map.put("message", SHOP_MESSAGE_SUCCESS);
		}

		return map;
	}
}