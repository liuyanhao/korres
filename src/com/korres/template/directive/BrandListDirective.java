package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Brand;
import com.korres.service.BrandService;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("brandListDirective")
public class BrandListDirective extends BaseDirective {
	private static final String BRANDS = "brands";

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<Filter> lif = getFilters(params, Brand.class, new String[0]);
		List<Order> lio = getOrders(params, new String[0]);
		List<Brand> lib = null;
		if (bool) {
			lib = this.brandService.findList(count, lif, lio, cacheRegion);
		} else {
			lib = this.brandService.findList(count, lif, lio);
		}

		setVariables(BRANDS, lib, env, body);
	}
}