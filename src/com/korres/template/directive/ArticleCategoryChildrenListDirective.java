package com.korres.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.korres.entity.ArticleCategory;
import com.korres.service.ArticleCategoryService;
import com.korres.util.FreemarkerUtils;
import org.springframework.stereotype.Component;

@Component("articleCategoryChildrenListDirective")
public class ArticleCategoryChildrenListDirective extends BaseDirective {
	private static final String ARTICLE_CATEGORY_ID = "articleCategoryId";
	private static final String ARTICLE_CATEGORIES = "articleCategories";

	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Long articleCategoryId = null;
		try {
			articleCategoryId = (Long) FreemarkerUtils.getParameter(
					ARTICLE_CATEGORY_ID, Long.class, params);
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}

		ArticleCategory articleCategory = this.articleCategoryService
				.find(articleCategoryId);
		List<ArticleCategory> liac = null;
		if ((articleCategoryId != null) && (articleCategory == null)) {
			liac = new ArrayList<ArticleCategory>();
		} else {
			boolean bool = useCache(env, params);
			String cacheRegion = cacheRegion(env, params);
			Integer count = this.getCount(params);
			if (bool) {
				liac = this.articleCategoryService.findChildren(
						articleCategory, count, cacheRegion);
			} else {
				liac = this.articleCategoryService.findChildren(
						articleCategory, count);
			}
		}

		setVariables(ARTICLE_CATEGORIES, liac, env, body);
	}
}