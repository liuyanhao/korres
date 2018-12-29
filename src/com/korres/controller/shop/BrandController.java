package com.korres.controller.shop;

import javax.annotation.Resource;
import com.korres.entity.Brand;
import com.korres.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;
import com.korres.ResourceNotFoundException;

@Controller("shopBrandController")
@RequestMapping( { "/brand" })
public class BrandController extends BaseController {
	private static final int PAGE_SIZE = 40;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@RequestMapping(value = { "/list/{pageNumber}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(@PathVariable Integer pageNumber, ModelMap model) {
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.brandService.findPage(pageable));

		return "/shop/brand/list";
	}

	@RequestMapping(value = { "/content/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String content(@PathVariable Long id, ModelMap model) {
		Brand brand = (Brand) this.brandService.find(id);
		if (brand == null) {
			throw new ResourceNotFoundException();
		}

		model.addAttribute("brand", brand);

		return "/shop/brand/content";
	}
}