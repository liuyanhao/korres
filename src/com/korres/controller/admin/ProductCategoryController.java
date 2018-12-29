package com.korres.controller.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.korres.entity.Product;
import com.korres.entity.ProductCategory;
import com.korres.service.BrandService;
import com.korres.service.ProductCategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;

@Controller("adminProductCategoryController")
@RequestMapping( { "/admin/product_category" })
public class ProductCategoryController extends BaseController {

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findTree());
		model.addAttribute("brands", this.brandService.findAll());

		return "/admin/product_category/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(ProductCategory productCategory, Long parentId,
			Long[] brandIds, RedirectAttributes redirectAttributes) {
		productCategory.setParent((ProductCategory) this.productCategoryService
				.find(parentId));
		productCategory.setBrands(new HashSet(this.brandService
				.findList(brandIds)));
		if (!validator(productCategory, new Class[0])){
			return "/admin/common/error";
		}
		productCategory.setTreePath(null);
		productCategory.setGrade(null);
		productCategory.setChildren(null);
		productCategory.setProducts(null);
		productCategory.setParameterGroups(null);
		productCategory.setAttributes(null);
		productCategory.setPromotions(null);
		this.productCategoryService.save(productCategory);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		ProductCategory localProductCategory = (ProductCategory) this.productCategoryService
				.find(id);
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findTree());
		model.addAttribute("brands", this.brandService.findAll());
		model.addAttribute("productCategory", localProductCategory);
		model.addAttribute("children", this.productCategoryService
				.findChildren(localProductCategory));

		return "/admin/product_category/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(ProductCategory productCategory, Long parentId,
			Long[] brandIds, RedirectAttributes redirectAttributes) {
		productCategory.setParent((ProductCategory) this.productCategoryService
				.find(parentId));
		productCategory.setBrands(new HashSet(this.brandService
				.findList(brandIds)));
		if (!validator(productCategory, new Class[0])) {
			return "/admin/common/error";
		}

		if (productCategory.getParent() != null) {
			ProductCategory localProductCategory = productCategory.getParent();
			if (localProductCategory.equals(productCategory)) {
				return "/admin/common/error";
			}

			List<ProductCategory> lipc = this.productCategoryService
					.findChildren(localProductCategory);
			if ((lipc != null) && (lipc.contains(localProductCategory))) {
				return "/admin/common/error";
			}
		}

		this.productCategoryService.update(productCategory, new String[] {
				"treePath", "grade", "children", "products", "parameterGroups",
				"attributes", "promotions" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ModelMap model) {
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findTree());

		return "/admin/product_category/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long id) {
		ProductCategory productCategory = this.productCategoryService.find(id);
		if (productCategory == null) {
			return ADMIN_MESSAGE_ERROR;
		}

		Set<ProductCategory> spc = productCategory.getChildren();
		if ((spc != null) && (!spc.isEmpty())) {
			return Message.error(
					"admin.productCategory.deleteExistChildrenNotAllowed",
					new Object[0]);
		}

		Set<Product> sp = productCategory.getProducts();
		if ((sp != null) && (!sp.isEmpty())) {
			return Message.error(
					"admin.productCategory.deleteExistProductNotAllowed",
					new Object[0]);
		}

		this.productCategoryService.delete(id);

		return ADMIN_MESSAGE_SUCCESS;
	}
}