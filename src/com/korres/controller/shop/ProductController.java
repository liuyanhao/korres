package com.korres.controller.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.entity.Attribute;
import com.korres.entity.Brand;
import com.korres.entity.Product;
import com.korres.entity.Product.ProductOrderType;
import com.korres.entity.ProductCategory;
import com.korres.entity.Promotion;
import com.korres.entity.Tag;
import com.korres.service.BrandService;
import com.korres.service.ProductCategoryService;
import com.korres.service.ProductService;
import com.korres.service.PromotionService;
import com.korres.service.SearchService;
import com.korres.service.TagService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Pageable;
import com.korres.ResourceNotFoundException;

@Controller("shopProductController")
@RequestMapping({ "/product" })
public class ProductController extends BaseController {

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "searchServiceImpl")
	private SearchService searchService;

	@RequestMapping(value = { "/history" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public List<Product> history(Long[] ids) {
		return this.productService.findList(ids);
	}

	@RequestMapping(value = { "/list/{productCategoryId}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(@PathVariable Long productCategoryId, Long brandId,
			Long promotionId, Long[] tagIds, BigDecimal startPrice,
			BigDecimal endPrice, ProductOrderType orderType,
			Integer pageNumber, Integer pageSize, HttpServletRequest request,
			ModelMap model) {
		ProductCategory productCategory = this.productCategoryService
				.find(productCategoryId);
		if (productCategory == null) {
			throw new ResourceNotFoundException();
		}

		Brand brand = (Brand) this.brandService.find(brandId);
		Promotion promotion = this.promotionService.find(promotionId);
		List<Tag> litag = this.tagService.findList(tagIds);
		Map<Attribute, String> map = new HashMap<Attribute, String>();
		if (productCategory != null) {
			Set<Attribute> sa = productCategory.getAttributes();
			Iterator<Attribute> iterator = sa.iterator();
			while (iterator.hasNext()) {
				Attribute attribute = iterator.next();
				String str = request.getParameter("attribute_"
						+ attribute.getId());
				if (StringUtils.isNotEmpty(str))
					map.put(attribute, str);
			}
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", ProductOrderType.values());
		model.addAttribute("productCategory", productCategory);
		model.addAttribute("brand", brand);
		model.addAttribute("promotion", promotion);
		model.addAttribute("tags", litag);
		model.addAttribute("attributeValue", map);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("page", this.productService.findPage(
				productCategory, brand, promotion, litag, map, startPrice,
				endPrice, Boolean.valueOf(true), Boolean.valueOf(true), null,
				Boolean.valueOf(false), null, null, orderType, pageable));

		return "/shop/product/list";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Long brandId, Long promotionId, Long[] tagIds,
			BigDecimal startPrice, BigDecimal endPrice,
			ProductOrderType orderType, Integer pageNumber, Integer pageSize,
			HttpServletRequest request, ModelMap model) {
		Brand brand = this.brandService.find(brandId);
		Promotion promotion = this.promotionService.find(promotionId);
		List<Tag> tags = this.tagService.findList(tagIds);
		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", ProductOrderType.values());
		model.addAttribute("brand", brand);
		model.addAttribute("promotion", promotion);
		model.addAttribute("tags", tags);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("page", this.productService.findPage(null, brand,
				promotion, tags, null, startPrice, endPrice,
				Boolean.valueOf(true), Boolean.valueOf(true), null,
				Boolean.valueOf(false), null, null, orderType, pageable));

		return "/shop/product/list";
	}

	@RequestMapping(value = { "/search" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String search(String keyword, BigDecimal startPrice,
			BigDecimal endPrice, ProductOrderType orderType,
			Integer pageNumber, Integer pageSize, ModelMap model) {
		if (StringUtils.isEmpty(keyword)) {
			return SHOP_COMMON_ERROR;
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", ProductOrderType.values());
		model.addAttribute("productKeyword", keyword);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("page", this.searchService.search(keyword,
				startPrice, endPrice, orderType, pageable));

		return "shop/product/search";
	}

	@RequestMapping(value = { "/hits/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Long hits(@PathVariable Long id) {
		return Long.valueOf(this.productService.viewHits(id));
	}
}