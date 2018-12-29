package com.korres.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.GiftItem;
import com.korres.entity.Product;
import com.korres.entity.Promotion;
import com.korres.entity.Promotion.PromotionOperator;
import com.korres.service.BrandService;
import com.korres.service.CouponService;
import com.korres.service.MemberRankService;
import com.korres.service.ProductCategoryService;
import com.korres.service.ProductService;
import com.korres.service.PromotionService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminPromotionController")
@RequestMapping( { "/admin/promotion" })
public class PromotionController extends BaseController {

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "couponServiceImpl")
	private CouponService couponService;

	@RequestMapping(value = { "/product_select" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public List<Map<String, Object>> productSelect(String q) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotEmpty(q)) {
			List<Product> lip = this.productService.search(q, Boolean
					.valueOf(false), Integer.valueOf(20));
			Iterator<Product> iterator = lip.iterator();
			while (iterator.hasNext()) {
				Product product = iterator.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", product.getId());
				map.put("sn", product.getSn());
				map.put("fullName", product.getFullName());
				map.put("path", product.getPath());
				list.add(map);
			}
		}

		return list;
	}

	@RequestMapping(value = { "/gift_select" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public List<Map<String, Object>> giftSelect(String q) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotEmpty(q)) {
			List<Product> lip = this.productService.search(q, Boolean
					.valueOf(true), Integer.valueOf(20));
			Iterator<Product> iterator = lip.iterator();
			while (iterator.hasNext()) {
				Product product = iterator.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", product.getId());
				map.put("sn", product.getSn());
				map.put("fullName", product.getFullName());
				map.put("path", product.getPath());

				list.add(map);
			}
		}

		return list;
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("operators", PromotionOperator.values());
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("productCategories", this.productCategoryService
				.findAll());
		model.addAttribute("brands", this.brandService.findAll());
		model.addAttribute("coupons", this.couponService.findAll());

		return "/admin/promotion/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Promotion promotion, Long[] memberRankIds,
			Long[] productCategoryIds, Long[] brandIds, Long[] couponIds,
			Long[] productIds, RedirectAttributes redirectAttributes) {
		promotion.setMemberRanks(new HashSet(this.memberRankService
				.findList(memberRankIds)));
		promotion.setProductCategories(new HashSet(this.productCategoryService
				.findList(productCategoryIds)));
		promotion.setBrands(new HashSet(this.brandService.findList(brandIds)));
		promotion
				.setCoupons(new HashSet(this.couponService.findList(couponIds)));
		Iterator<Product> iterator = this.productService.findList(productIds)
				.iterator();
		while (iterator.hasNext()) {
			Product product = iterator.next();
			if (!product.getIsGift().booleanValue())
				promotion.getProducts().add(product);
		}
		Iterator<GiftItem> iterator2 = promotion.getGiftItems().iterator();
		while (iterator2.hasNext()) {
			GiftItem giftItem = iterator2.next();
			if ((giftItem == null) || (giftItem.getGift() == null)
					|| (giftItem.getGift().getId() == null)) {
				iterator2.remove();
			} else {
				giftItem.setGift(this.productService.find(giftItem.getGift()
						.getId()));
				giftItem.setPromotion(promotion);
			}
		}
		if (!validator(promotion, new Class[0])) {
			return "/admin/common/error";
		}

		if ((promotion.getBeginDate() != null)
				&& (promotion.getEndDate() != null)
				&& (promotion.getBeginDate().after(promotion.getEndDate()))) {
			return "/admin/common/error";
		}

		if ((promotion.getStartPrice() != null)
				&& (promotion.getEndPrice() != null)
				&& (promotion.getStartPrice()
						.compareTo(promotion.getEndPrice()) > 0)) {
			return "/admin/common/error";
		}

		if ((promotion.getPriceOperator() == PromotionOperator.divide)
				&& (promotion.getPriceValue() != null)
				&& (promotion.getPriceValue().compareTo(new BigDecimal(0)) == 0)) {
			return "/admin/common/error";
		}

		if ((promotion.getPointOperator() == PromotionOperator.divide)
				&& (promotion.getPointValue() != null)
				&& (promotion.getPointValue().compareTo(new BigDecimal(0)) == 0)) {
			return "/admin/common/error";
		}

		this.promotionService.save(promotion);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("promotion", this.promotionService.find(id));
		model.addAttribute("operators", PromotionOperator.values());
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("productCategories", this.productCategoryService
				.findAll());
		model.addAttribute("brands", this.brandService.findAll());
		model.addAttribute("coupons", this.couponService.findAll());

		return "/admin/promotion/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Promotion promotion, Long[] memberRankIds,
			Long[] productCategoryIds, Long[] brandIds, Long[] couponIds,
			Long[] productIds, RedirectAttributes redirectAttributes) {
		promotion.setMemberRanks(new HashSet(this.memberRankService
				.findList(memberRankIds)));
		promotion.setProductCategories(new HashSet(this.productCategoryService
				.findList(productCategoryIds)));
		promotion.setBrands(new HashSet(this.brandService.findList(brandIds)));
		promotion
				.setCoupons(new HashSet(this.couponService.findList(couponIds)));

		Iterator<Product> iterator = this.productService.findList(productIds)
				.iterator();
		while (iterator.hasNext()) {
			Product product = (Product) iterator.next();
			if (!product.getIsGift().booleanValue()) {
				promotion.getProducts().add(product);
			}
		}

		Iterator<GiftItem> iterator2 = promotion.getGiftItems().iterator();
		while (iterator2.hasNext()) {
			GiftItem giftItem = iterator2.next();
			if ((giftItem == null) || (giftItem.getGift() == null)
					|| (giftItem.getGift().getId() == null)) {
				iterator2.remove();
			} else {
				giftItem.setGift((Product) this.productService.find(giftItem
						.getGift().getId()));
				giftItem.setPromotion(promotion);
			}
		}
		if (!validator(promotion, new Class[0])) {
			return "/admin/common/error";
		}

		if ((promotion.getBeginDate() != null)
				&& (promotion.getEndDate() != null)
				&& (promotion.getBeginDate().after(promotion.getEndDate()))) {
			return "/admin/common/error";
		}

		if ((promotion.getPriceOperator() == PromotionOperator.divide)
				&& (promotion.getPriceValue() != null)
				&& (promotion.getPriceValue().compareTo(new BigDecimal(0)) == 0)) {
			return "/admin/common/error";
		}

		if ((promotion.getPointOperator() == PromotionOperator.divide)
				&& (promotion.getPointValue() != null)
				&& (promotion.getPointValue().compareTo(new BigDecimal(0)) == 0)) {
			return "/admin/common/error";
		}

		this.promotionService.update(promotion);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.promotionService.findPage(pageable));

		return "/admin/promotion/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.promotionService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}