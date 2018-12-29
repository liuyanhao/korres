package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;
import com.korres.entity.Tag;
import com.korres.service.TagService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("tagListDirective")
public class TagListDirective extends BaseDirective {
	private static final String TAGS = "tags";

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<Filter> lif = getFilters(params, Tag.class, new String[0]);
		List<Order> lio = getOrders(params, new String[0]);
		List<Tag> litag = null;
		if (bool) {
			litag = this.tagService.findList(count, lif, lio, cacheRegion);
		} else {
			litag = this.tagService.findList(count, lif, lio);
		}

		setVariables(TAGS, litag, env, body);
	}
}