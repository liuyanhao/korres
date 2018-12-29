package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Promotion;
import com.korres.service.PromotionService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("promotionListDirective")
public class PromotionListDirective extends BaseDirective {
	private static final String HASBEGUN = "hasBegun";
	private static final String HASENDED = "hasEnded";
	private static final String PROMOTIONS = "promotions";

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Boolean hasBegun = (Boolean) FreemarkerUtils.getParameter(HASBEGUN,
				Boolean.class, params);
		Boolean hasEnded = (Boolean) FreemarkerUtils.getParameter(HASENDED,
				Boolean.class, params);
		boolean useCache = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<Filter> lif = getFilters(params, Promotion.class, new String[0]);
		List<Order> lio = getOrders(params, new String[0]);
		List<Promotion> lip = null;
		if (useCache) {
			lip = this.promotionService.findList(hasBegun, hasEnded, count,
					lif, lio, cacheRegion);
		} else {
			lip = this.promotionService.findList(hasBegun, hasEnded, count,
					lif, lio);
		}

		setVariables(PROMOTIONS, lip, env, body);
	}
}