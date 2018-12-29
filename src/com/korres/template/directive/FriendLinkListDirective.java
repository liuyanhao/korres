package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.FriendLink;
import com.korres.service.FriendLinkService;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("friendLinkListDirective")
public class FriendLinkListDirective extends BaseDirective {
	private static final String FRIEND_LINKS = "friendLinks";

	@Resource(name = "friendLinkServiceImpl")
	private FriendLinkService friendLinkService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<Filter> lif = getFilters(params, FriendLink.class, new String[0]);
		List<Order> lio = getOrders(params, new String[0]);
		List<FriendLink> lifl = null;
		if (bool) {
			lifl = this.friendLinkService.findList(count, lif, lio,
					cacheRegion);
		} else {
			lifl = this.friendLinkService.findList(count, lif, lio);
		}

		setVariables(FRIEND_LINKS, lifl, env, body);
	}
}