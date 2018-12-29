package com.korres.template.directive;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Article;
import com.korres.entity.Attribute;
import com.korres.entity.Brand;
import com.korres.entity.Product;
import com.korres.entity.ProductCategory;
import com.korres.entity.Promotion;
import com.korres.entity.Tag;
import com.korres.entity.Product.ProductOrderType;
import com.korres.service.AttributeService;
import com.korres.service.BrandService;
import com.korres.service.ProductCategoryService;
import com.korres.service.ProductService;
import com.korres.service.PromotionService;
import com.korres.service.TagService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("productListDirective")
public class ProductListDirective extends BaseDirective {
	private static final String PRODUCT_CATEGORY_ID = "productCategoryId";
	private static final String BRAND_ID = "brandId";
	private static final String PROMOTION_ID = "promotionId";
	private static final String TAG_IDS = "tagIds";
	private static final String ATTRIBUTEVALUE = "attributeValue";
	private static final String START_PRICE = "startPrice";
	private static final String END_PRICE = "endPrice";
	private static final String ORDER_TYPE = "orderType";
	private static final String PRODUCTS = "products";

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@Resource(name = "attributeServiceImpl")
	private AttributeService attributeService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Long productCategoryId = (Long) FreemarkerUtils.getParameter(
				PRODUCT_CATEGORY_ID, Long.class, params);
		Long brandId = (Long) FreemarkerUtils.getParameter(BRAND_ID,
				Long.class, params);
		Long promotionId = (Long) FreemarkerUtils.getParameter(PROMOTION_ID,
				Long.class, params);
		Long[] tagIds = (Long[]) FreemarkerUtils.getParameter(TAG_IDS,
				java.lang.Long[].class, params);
		Map attributeValue = (Map) FreemarkerUtils.getParameter(ATTRIBUTEVALUE,
				Map.class, params);
		BigDecimal startPrice = (BigDecimal) FreemarkerUtils.getParameter(
				START_PRICE, BigDecimal.class, params);
		BigDecimal endPrice = (BigDecimal) FreemarkerUtils.getParameter(
				END_PRICE, BigDecimal.class, params);
		ProductOrderType orderType = FreemarkerUtils.getParameter(ORDER_TYPE,
				ProductOrderType.class, params);
		ProductCategory productCategory = this.productCategoryService
				.find(productCategoryId);
		Brand brand = this.brandService.find(brandId);
		Promotion promotion = this.promotionService.find(promotionId);
		List<Tag> litag = this.tagService.findList(tagIds);
		Map<Attribute, String> map = new HashMap<Attribute, String>();

		if (attributeValue != null) {
			Iterator iterator = attributeValue.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				Attribute attribute = this.attributeService.find((Long) entry
						.getKey());
				if (attribute != null) {
					map.put(attribute, (String) entry.getValue());
				}
			}
		}

		List<Product> lip = null;
		if (((productCategoryId != null) && (productCategory == null))
				|| ((brandId != null) && (brand == null))
				|| ((promotionId != null) && (promotion == null))
				|| ((tagIds != null) && (litag.isEmpty()))
				|| ((attributeValue != null) && (map.isEmpty()))) {
			lip = new ArrayList<Product>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = cacheRegion(env, params);
			Integer count = this.getCount(params);
			List<Filter> lif = getFilters(params, Article.class, new String[0]);
			List<Order> lio = getOrders(params, new String[0]);
			if (useCache) {
				lip = this.productService.findList(productCategory, brand,
						promotion, litag, map, startPrice, endPrice, Boolean
								.valueOf(true), Boolean.valueOf(true), null,
						Boolean.valueOf(false), null, null, orderType, count,
						lif, lio, cacheRegion);
			} else {
				lip = this.productService.findList(productCategory, brand,
						promotion, litag, map, startPrice, endPrice, Boolean
								.valueOf(true), Boolean.valueOf(true), null,
						Boolean.valueOf(false), null, null, orderType, count,
						lif, lio);
			}
			
			System.out.println("count:" + count + ",product list size:" + lip.size());
		}

		setVariables(PRODUCTS, lip, env, body);
		
		
	}
}