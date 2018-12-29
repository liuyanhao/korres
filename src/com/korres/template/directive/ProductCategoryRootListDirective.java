package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.ProductCategory;
import com.korres.service.ProductCategoryService;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("productCategoryRootListDirective")
public class ProductCategoryRootListDirective extends BaseDirective {
	private static final String PRODUCT_CATEGORIES = "productCategories";

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean useCache = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<ProductCategory> lipc = null;
		if (useCache) {
			lipc = this.productCategoryService.findRoots(count, cacheRegion);
		} else {
			lipc = this.productCategoryService.findRoots(count);
		}

		setVariables(PRODUCT_CATEGORIES, lipc, env, body);
	}
}