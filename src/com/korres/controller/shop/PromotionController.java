package com.korres.controller.shop;

import javax.annotation.Resource;
import com.korres.entity.Promotion;
import com.korres.service.PromotionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.ResourceNotFoundException;

@Controller("shopPromotionController")
@RequestMapping({ "/promotion" })
public class PromotionController extends BaseController {

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@RequestMapping(value = { "/content/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String content(@PathVariable Long id, ModelMap model) {
		Promotion promotion = this.promotionService.find(id);
		if (promotion == null) {
			throw new ResourceNotFoundException();
		}

		model.addAttribute("promotion", promotion);

		return "/shop/promotion/content";
	}
}