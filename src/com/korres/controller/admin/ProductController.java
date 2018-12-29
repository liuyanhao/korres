package com.korres.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.entity.Attribute;
import com.korres.entity.Brand;
import com.korres.entity.Goods;
import com.korres.entity.MemberRank;
import com.korres.entity.Parameter;
import com.korres.entity.ParameterGroup;
import com.korres.entity.Product;
import com.korres.entity.ProductCategory;
import com.korres.entity.ProductImage;
import com.korres.entity.Promotion;
import com.korres.entity.Specification;
import com.korres.entity.SpecificationValue;
import com.korres.entity.Tag;
import com.korres.entity.Product.ProductOrderType;
import com.korres.entity.Tag.TagType;
import com.korres.service.BrandService;
import com.korres.service.FileService;
import com.korres.service.GoodsService;
import com.korres.service.MemberRankService;
import com.korres.service.ProductCategoryService;
import com.korres.service.ProductImageService;
import com.korres.service.ProductService;
import com.korres.service.PromotionService;
import com.korres.service.SpecificationService;
import com.korres.service.SpecificationValueService;
import com.korres.service.TagService;
import com.korres.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;
import com.korres.Setting;
import com.korres.FileInfo.FileInfoFileType;

@Controller("adminProductController")
@RequestMapping( { "/admin/product" })
public class ProductController extends BaseController {

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "productImageServiceImpl")
	private ProductImageService productImageService;

	@Resource(name = "specificationServiceImpl")
	private SpecificationService specificationService;

	@Resource(name = "specificationValueServiceImpl")
	private SpecificationValueService specificationValueService;

	@Resource(name = "fileServiceImpl")
	private FileService fileService;

	@RequestMapping(value = { "/check_sn" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkSn(String previousSn, String sn) {
		if (StringUtils.isEmpty(sn)) {
			return false;
		}

		return this.productService.snUnique(previousSn, sn);
	}

	@RequestMapping(value = { "/parameter_groups" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Set<ParameterGroup> parameterGroups(Long id) {
		ProductCategory productCategory = (ProductCategory) this.productCategoryService
				.find(id);

		return productCategory.getParameterGroups();
	}

	@RequestMapping(value = { "/attributes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Set<Attribute> attributes(Long id) {
		ProductCategory productCategory = (ProductCategory) this.productCategoryService
				.find(id);

		return productCategory.getAttributes();
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findTree());
		model.addAttribute("brands", this.brandService.findAll());
		model.addAttribute("tags", this.tagService.findList(TagType.product));
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("specifications", this.specificationService
				.findAll());

		return "/admin/product/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Product product, Long productCategoryId, Long brandId,
			Long[] tagIds, Long[] specificationIds, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		Iterator<ProductImage> iterator1 = product.getProductImages()
				.iterator();
		while (iterator1.hasNext()) {
			ProductImage productImage = (ProductImage) iterator1.next();
			if ((productImage == null) || (productImage.isEmpty())) {
				iterator1.remove();
			} else if ((productImage.getFile() != null)
					&& (!productImage.getFile().isEmpty())
					&& (!this.fileService.isValid(FileInfoFileType.image,
							productImage.getFile()))) {
				setRedirectAttributes(redirectAttributes, Message.error(
						"admin.upload.invalid", new Object[0]));

				return "redirect:add.jhtml";
			}
		}

		product
				.setProductCategory((ProductCategory) this.productCategoryService
						.find(productCategoryId));
		product.setBrand((Brand) this.brandService.find(brandId));
		product.setTags(new HashSet(this.tagService.findList(tagIds)));

		if (!validator(product, new Class[0])) {
			return "/admin/common/error";
		}

		if ((StringUtils.isNotEmpty(product.getSn()))
				&& (this.productService.snExists(product.getSn()))) {
			return "/admin/common/error";
		}

		if (product.getMarketPrice() == null) {
			BigDecimal marketPrice = IIIllIlI(product.getPrice());
			product.setMarketPrice(marketPrice);
		}

		if (product.getPoint() == null) {
			long point = IIIllIll(product.getPrice());
			product.setPoint(Long.valueOf(point));
		}

		product.setFullName(null);
		product.setAllocatedStock(Integer.valueOf(0));
		product.setScore(Float.valueOf(0.0F));
		product.setTotalScore(Long.valueOf(0L));
		product.setScoreCount(Long.valueOf(0L));
		product.setHits(Long.valueOf(0L));
		product.setWeekHits(Long.valueOf(0L));
		product.setMonthHits(Long.valueOf(0L));
		product.setSales(Long.valueOf(0L));
		product.setWeekSales(Long.valueOf(0L));
		product.setMonthSales(Long.valueOf(0L));
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		product.setReviews(null);
		product.setConsultations(null);
		product.setFavoriteMembers(null);
		product.setPromotions(null);
		product.setCartItems(null);
		product.setOrderItems(null);
		product.setGiftItems(null);
		product.setProductNotifies(null);

		Iterator<MemberRank> iterator2 = this.memberRankService.findAll()
				.iterator();
		while (iterator2.hasNext()) {
			MemberRank memberRank = iterator2.next();
			String memberPriceStr = request.getParameter("memberPrice_"
					+ memberRank.getId());
			if ((StringUtils.isNotEmpty(memberPriceStr) && (new BigDecimal(
					memberPriceStr).compareTo(new BigDecimal(0)) >= 0))) {
				product.getMemberPrice().put(memberRank,
						new BigDecimal(memberPriceStr));
			} else {
				product.getMemberPrice().remove(memberRank);
			}
		}

		Iterator<ProductImage> iterator3 = product.getProductImages()
				.iterator();
		while (iterator3.hasNext()) {
			ProductImage productImage = iterator3.next();
			this.productImageService.build(productImage);
		}

		Collections.sort(product.getProductImages());
		if ((product.getImage() == null) && (product.getThumbnail() != null)) {
			product.setImage(product.getThumbnail());
		}

		Iterator<ParameterGroup> iterator4 = product.getProductCategory()
				.getParameterGroups().iterator();
		while (iterator4.hasNext()) {
			ParameterGroup parameterGroup = iterator4.next();
			Iterator<Parameter> iparameter = parameterGroup.getParameters()
					.iterator();
			while (iparameter.hasNext()) {
				Parameter parameter = (Parameter) iparameter.next();
				String parameterStr = request.getParameter("parameter_"
						+ parameter.getId());
				if (StringUtils.isNotEmpty(parameterStr)) {
					product.getParameterValue().put(parameter, parameterStr);
				} else {
					product.getParameterValue().remove(parameter);
				}
			}
		}

		Iterator<Attribute> iterator5 = product.getProductCategory()
				.getAttributes().iterator();
		while (iterator5.hasNext()) {
			Attribute attribute = (Attribute) iterator5.next();
			String attributeStr = request.getParameter("attribute_"
					+ attribute.getId());
			if (StringUtils.isNotEmpty(attributeStr)) {
				product.setAttributeValue(attribute, attributeStr);
			} else {
				product.setAttributeValue(attribute, null);
			}
		}

		Goods goods = new Goods();
		List<Product> lip = new ArrayList<Product>();
		if ((specificationIds != null) && (specificationIds.length > 0)) {
			for (int i = 0; i < specificationIds.length; i++) {
				Specification specification = (Specification) this.specificationService
						.find(specificationIds[i]);
				String[] specificationStr = request
						.getParameterValues("specification_"
								+ specification.getId());
				if ((specificationStr != null) && (specificationStr.length > 0))
					for (int j = 0; j < specificationStr.length; j++) {
						if (i == 0)
							if (j == 0) {
								product.setGoods(goods);
								product.setSpecifications(new HashSet());
								product.setSpecificationValues(new HashSet());
								lip.add(product);
							} else {
								Product localProduct = new Product();
								BeanUtils.copyProperties(product, localProduct);
								localProduct.setId(null);
								localProduct.setCreateDate(null);
								localProduct.setModifyDate(null);
								localProduct.setSn(null);
								localProduct.setFullName(null);
								localProduct.setAllocatedStock(Integer
										.valueOf(0));
								localProduct.setIsList(Boolean.valueOf(false));
								localProduct.setScore(Float.valueOf(0.0F));
								localProduct.setTotalScore(Long.valueOf(0L));
								localProduct.setScoreCount(Long.valueOf(0L));
								localProduct.setHits(Long.valueOf(0L));
								localProduct.setWeekHits(Long.valueOf(0L));
								localProduct.setMonthHits(Long.valueOf(0L));
								localProduct.setSales(Long.valueOf(0L));
								localProduct.setWeekSales(Long.valueOf(0L));
								localProduct.setMonthSales(Long.valueOf(0L));
								localProduct.setWeekHitsDate(new Date());
								localProduct.setMonthHitsDate(new Date());
								localProduct.setWeekSalesDate(new Date());
								localProduct.setMonthSalesDate(new Date());
								localProduct.setGoods(goods);
								localProduct.setReviews(null);
								localProduct.setConsultations(null);
								localProduct.setFavoriteMembers(null);
								localProduct.setSpecifications(new HashSet());
								localProduct
										.setSpecificationValues(new HashSet());
								localProduct.setPromotions(null);
								localProduct.setCartItems(null);
								localProduct.setOrderItems(null);
								localProduct.setGiftItems(null);
								localProduct.setProductNotifies(null);
								lip.add(localProduct);
							}
						Product localProduct = lip.get(j);
						SpecificationValue localSpecificationValue = (SpecificationValue) this.specificationValueService
								.find(Long.valueOf(specificationStr[j]));
						localProduct.getSpecifications().add(specification);
						localProduct.getSpecificationValues().add(
								localSpecificationValue);
					}
			}
		} else {
			product.setGoods(goods);
			product.setSpecifications(null);
			product.setSpecificationValues(null);
			lip.add(product);
		}

		goods.getProducts().clear();
		goods.getProducts().addAll(lip);
		this.goodsService.save(goods);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findTree());
		model.addAttribute("brands", this.brandService.findAll());
		model.addAttribute("tags", this.tagService.findList(TagType.product));
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("specifications", this.specificationService
				.findAll());
		model.addAttribute("product", this.productService.find(id));

		return "/admin/product/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Product product, Long productCategoryId, Long brandId,
			Long[] tagIds, Long[] specificationIds,
			Long[] specificationProductIds, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		Iterator<ProductImage> iterator = product.getProductImages().iterator();
		while (iterator.hasNext()) {
			ProductImage productImage = iterator.next();
			if ((productImage == null) || (productImage.isEmpty())) {
				iterator.remove();
			} else if ((productImage.getFile() != null)
					&& (!productImage.getFile().isEmpty())
					&& (!this.fileService.isValid(FileInfoFileType.image,
							productImage.getFile()))) {
				setRedirectAttributes(redirectAttributes, Message.error(
						"admin.upload.invalid", new Object[0]));
				return "redirect:edit.jhtml?id=" + product.getId();
			}
		}
		product.setProductCategory(this.productCategoryService
				.find(productCategoryId));
		product.setBrand(this.brandService.find(brandId));
		product.setTags(new HashSet(this.tagService.findList(tagIds)));
		if (!validator(product, new Class[0])) {
			return "/admin/common/error";
		}

		Product p = this.productService.find(product.getId());
		if (p == null) {
			return "/admin/common/error";
		}

		if ((StringUtils.isNotEmpty(product.getSn()))
				&& (!this.productService.snUnique(p.getSn(), product.getSn()))) {
			return "/admin/common/error";
		}

		if (product.getMarketPrice() == null) {
			BigDecimal marketPrice = IIIllIlI(product.getPrice());
			product.setMarketPrice(marketPrice);
		}
		if (product.getPoint() == null) {
			long point = IIIllIll(product.getPrice());
			product.setPoint(Long.valueOf(point));
		}

		Iterator<MemberRank> iterator2 = this.memberRankService.findAll()
				.iterator();
		while (iterator2.hasNext()) {
			MemberRank memberRank = iterator2.next();
			String memberPrice = request.getParameter("memberPrice_"
					+ memberRank.getId());
			if ((StringUtils.isNotEmpty(memberPrice))
					&& (new BigDecimal(memberPrice)
							.compareTo(new BigDecimal(0)) >= 0)) {
				product.getMemberPrice().put(memberRank,
						new BigDecimal(memberPrice));
			} else {
				product.getMemberPrice().remove(memberPrice);
			}
		}

		Iterator<ProductImage> iterator3 = product.getProductImages()
				.iterator();
		while (iterator3.hasNext()) {
			ProductImage productImage = iterator3.next();
			this.productImageService.build(productImage);
		}

		Collections.sort(product.getProductImages());
		if ((product.getImage() == null) && (product.getThumbnail() != null)) {
			product.setImage(product.getThumbnail());
		}

		Iterator<ParameterGroup> iterator4 = product.getProductCategory()
				.getParameterGroups().iterator();
		while (iterator4.hasNext()) {
			ParameterGroup parameterGroup = iterator4.next();
			Iterator<Parameter> iparameter = parameterGroup.getParameters()
					.iterator();
			while (iparameter.hasNext()) {
				Parameter parameter = iparameter.next();
				String parameterStr = request.getParameter("parameter_"
						+ parameter.getId());
				if (StringUtils.isNotEmpty(parameterStr)) {
					product.getParameterValue().put(parameter, parameterStr);
				} else {
					product.getParameterValue().remove(parameter);
				}
			}
		}

		Iterator<Attribute> iterator5 = product.getProductCategory()
				.getAttributes().iterator();
		while (iterator5.hasNext()) {
			Attribute attribute = iterator5.next();
			String attributeStr = request.getParameter("attribute_"
					+ attribute.getId());
			if (StringUtils.isNotEmpty(attributeStr)) {
				product.setAttributeValue(attribute, attributeStr);
			} else {
				product.setAttributeValue(attribute, null);
			}
		}

		Goods goods = p.getGoods();
		List<Product> lip = new ArrayList<Product>();
		if ((specificationIds != null) && (specificationIds.length > 0)) {
			for (int i = 0; i < specificationIds.length; i++) {
				Specification specification = this.specificationService
						.find(specificationIds[i]);
				String[] lispecification = request
						.getParameterValues("specification_"
								+ specification.getId());
				if ((lispecification != null) && (lispecification.length > 0))
					for (int j = 0; j < lispecification.length; j++) {
						if (i == 0)
							if (j == 0) {
								BeanUtils.copyProperties(product, p,
										new String[] { "id", "createDate",
												"modifyDate", "fullName",
												"allocatedStock", "score",
												"totalScore", "scoreCount",
												"hits", "weekHits",
												"monthHits", "sales",
												"weekSales", "monthSales",
												"weekHitsDate",
												"monthHitsDate",
												"weekSalesDate",
												"monthSalesDate", "goods",
												"reviews", "consultations",
												"favoriteMembers",
												"specifications",
												"specificationValues",
												"promotions", "cartItems",
												"orderItems", "giftItems",
												"productNotifies" });
								p.setSpecifications(new HashSet());
								p.setSpecificationValues(new HashSet());
								lip.add(p);
							} else if ((specificationProductIds != null)
									&& (j < specificationProductIds.length)) {
								Product localProduct = this.productService
										.find(specificationProductIds[j]);
								if (localProduct.getGoods() != goods) {
									return "/admin/common/error";
								}

								localProduct.setSpecifications(new HashSet());
								localProduct
										.setSpecificationValues(new HashSet());
								lip.add(localProduct);
							} else {
								Product localProduct = new Product();
								BeanUtils.copyProperties(product, localProduct);
								localProduct.setId(null);
								localProduct.setCreateDate(null);
								localProduct.setModifyDate(null);
								localProduct.setSn(null);
								localProduct.setFullName(null);
								localProduct.setAllocatedStock(Integer
										.valueOf(0));
								localProduct.setIsList(Boolean.valueOf(false));
								localProduct.setScore(Float.valueOf(0.0F));
								localProduct.setTotalScore(Long.valueOf(0L));
								localProduct.setScoreCount(Long.valueOf(0L));
								localProduct.setHits(Long.valueOf(0L));
								localProduct.setWeekHits(Long.valueOf(0L));
								localProduct.setMonthHits(Long.valueOf(0L));
								localProduct.setSales(Long.valueOf(0L));
								localProduct.setWeekSales(Long.valueOf(0L));
								localProduct.setMonthSales(Long.valueOf(0L));
								localProduct.setWeekHitsDate(new Date());
								localProduct.setMonthHitsDate(new Date());
								localProduct.setWeekSalesDate(new Date());
								localProduct.setMonthSalesDate(new Date());
								localProduct.setGoods(goods);
								localProduct.setReviews(null);
								localProduct.setConsultations(null);
								localProduct.setFavoriteMembers(null);
								localProduct.setSpecifications(new HashSet());
								localProduct
										.setSpecificationValues(new HashSet());
								localProduct.setPromotions(null);
								localProduct.setCartItems(null);
								localProduct.setOrderItems(null);
								localProduct.setGiftItems(null);
								localProduct.setProductNotifies(null);
								lip.add(localProduct);
							}

						Product localProduct = (Product) lip.get(j);
						SpecificationValue localSpecificationValue = (SpecificationValue) this.specificationValueService
								.find(Long.valueOf(lispecification[j]));
						localProduct.getSpecifications().add(specification);
						localProduct.getSpecificationValues().add(
								localSpecificationValue);
					}
			}
		} else {
			product.setSpecifications(null);
			product.setSpecificationValues(null);
			BeanUtils.copyProperties(product, p, new String[] { "id",
					"createDate", "modifyDate", "fullName", "allocatedStock",
					"score", "totalScore", "scoreCount", "hits", "weekHits",
					"monthHits", "sales", "weekSales", "monthSales",
					"weekHitsDate", "monthHitsDate", "weekSalesDate",
					"monthSalesDate", "goods", "reviews", "consultations",
					"favoriteMembers", "promotions", "cartItems", "orderItems",
					"giftItems", "productNotifies" });
			lip.add(p);
		}

		goods.getProducts().clear();
		goods.getProducts().addAll(lip);
		this.goodsService.update(goods);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Long productCategoryId, Long brandId, Long promotionId,
			Long tagId, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert,
			Pageable pageable, ModelMap model) {
		ProductCategory productCategory = (ProductCategory) this.productCategoryService
				.find(productCategoryId);
		Brand localBrand = (Brand) this.brandService.find(brandId);
		Promotion localPromotion = (Promotion) this.promotionService
				.find(promotionId);
		List<Tag> litag = this.tagService.findList(new Long[] { tagId });
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findTree());
		model.addAttribute("brands", this.brandService.findAll());
		model.addAttribute("promotions", this.promotionService.findAll());
		model.addAttribute("tags", this.tagService.findList(TagType.product));
		model.addAttribute("productCategoryId", productCategoryId);
		model.addAttribute("brandId", brandId);
		model.addAttribute("promotionId", promotionId);
		model.addAttribute("tagId", tagId);
		model.addAttribute("isMarketable", isMarketable);
		model.addAttribute("isList", isList);
		model.addAttribute("isTop", isTop);
		model.addAttribute("isGift", isGift);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("isStockAlert", isStockAlert);
		model.addAttribute("page", this.productService.findPage(
				productCategory, localBrand, localPromotion, litag, null, null,
				null, isMarketable, isList, isTop, isGift, isOutOfStock,
				isStockAlert, ProductOrderType.dateDesc, pageable));

		return "/admin/product/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.productService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}

	private BigDecimal IIIllIlI(BigDecimal paramBigDecimal) {
		Setting setting = SettingUtils.get();
		Double defaultMarketPriceScale = setting.getDefaultMarketPriceScale();
		return setting.setScale(paramBigDecimal.multiply(new BigDecimal(
				defaultMarketPriceScale.toString())));
	}

	private long IIIllIll(BigDecimal paramBigDecimal) {
		Setting setting = SettingUtils.get();
		Double defaultPointScale = setting.getDefaultPointScale();
		return paramBigDecimal.multiply(
				new BigDecimal(defaultPointScale.toString())).longValue();
	}
}