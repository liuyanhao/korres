package com.korres.template.directive;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Seo;
import com.korres.entity.Seo.SeoType;
import com.korres.service.SeoService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("seoDirective")
public class SeoDirective extends BaseDirective {
	private static final String TYPE = "type";
	private static final String SEO = "seo";

	@Resource(name = "seoServiceImpl")
	private SeoService seoService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		SeoType seoType = (SeoType) FreemarkerUtils.getParameter(TYPE,
				SeoType.class, params);
		boolean useCache = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Seo seo = null;
		if (useCache) {
			seo = this.seoService.find(seoType, cacheRegion);
		} else {
			seo = this.seoService.find(seoType);
		}

		setVariables(SEO, seo, env, body);
	}
}