package com.korres.controller.admin;

import javax.annotation.Resource;

import com.korres.entity.Brand;
import com.korres.entity.Brand.BrandType;
import com.korres.service.BrandService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminBrandController")
@RequestMapping({ "/admin/brand" })
public class BrandController extends BaseController {

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("types", BrandType.values());

		return "/admin/brand/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Brand brand, RedirectAttributes redirectAttributes) {
		if (!validator(brand, new Class[0])) {
			return "/admin/common/error";
		}

		if (brand.getType() == BrandType.text) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			return "/admin/common/error";
		}

		brand.setProducts(null);
		brand.setProductCategories(null);
		brand.setPromotions(null);
		this.brandService.save(brand);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", BrandType.values());
		model.addAttribute("brand", this.brandService.find(id));

		return "/admin/brand/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Brand brand, RedirectAttributes redirectAttributes) {
		if (!validator(brand, new Class[0])) {
			return "/admin/common/error";
		}

		if (brand.getType() == BrandType.text) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			return "/admin/common/error";
		}

		this.brandService.update(brand, new String[] { "products",
				"productCategories", "promotions" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.brandService.findPage(pageable));

		return "/admin/brand/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.brandService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}