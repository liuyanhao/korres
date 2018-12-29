package com.korres.controller.shop;

import javax.annotation.Resource;
import com.korres.service.ProductCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("shopProductCategoryController")
@RequestMapping({ "/product_category" })
public class ProductCategoryController extends BaseController {

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(ModelMap model) {
		model.addAttribute("rootProductCategories",
				this.productCategoryService.findRoots());

		return "/shop/product_category/index";
	}
}