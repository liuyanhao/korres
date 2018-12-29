package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Navigation;
import com.korres.service.NavigationService;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("navigationListDirective")
public class NavigationListDirective extends BaseDirective {
	private static final String NAVIGATIONS = "navigations";

	@Resource(name = "navigationServiceImpl")
	private NavigationService navigationService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<Filter> lif = getFilters(params, Navigation.class, new String[0]);
		List<Order> lio = getOrders(params, new String[0]);
		List<Navigation> lia = null;
		if (bool) {
			lia = this.navigationService.findList(count, lif, lio, cacheRegion);
		} else {
			lia = this.navigationService.findList(count, lif, lio);
		}

		setVariables(NAVIGATIONS, lia, env, body);
	}
}