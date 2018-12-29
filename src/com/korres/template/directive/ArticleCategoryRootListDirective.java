package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.ArticleCategory;
import com.korres.service.ArticleCategoryService;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("articleCategoryRootListDirective")
public class ArticleCategoryRootListDirective extends BaseDirective {
	private static final String ARTICLE_CATEGORIES = "articleCategories";

	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<ArticleCategory> liac;
		if (bool) {
			liac = this.articleCategoryService.findRoots(count, cacheRegion);
		} else {
			liac = this.articleCategoryService.findRoots(count);
		}

		setVariables(ARTICLE_CATEGORIES, liac, env, body);
	}
}