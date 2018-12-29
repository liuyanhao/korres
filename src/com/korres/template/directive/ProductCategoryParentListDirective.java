package com.korres.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.ProductCategory;
import com.korres.service.ProductCategoryService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("productCategoryParentListDirective")
public class ProductCategoryParentListDirective extends BaseDirective {
	private static final String PRODUCT_CATEGORY_ID = "productCategoryId";
	private static final String PRODUCT_CATEGORIES = "productCategories";

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Long productCategoryId = (Long) FreemarkerUtils.getParameter(
				PRODUCT_CATEGORY_ID, Long.class, params);
		ProductCategory productCategory = this.productCategoryService
				.find(productCategoryId);
		List<ProductCategory> lipc = null;
		if ((productCategoryId != null) && (productCategory == null)) {
			lipc = new ArrayList<ProductCategory>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = cacheRegion(env, params);
			Integer count = this.getCount(params);
			if (useCache) {
				lipc = this.productCategoryService.findParents(productCategory,
						count, cacheRegion);
			} else {
				lipc = this.productCategoryService.findParents(productCategory,
						count);
			}
		}

		setVariables(PRODUCT_CATEGORIES, lipc, env, body);
	}
}