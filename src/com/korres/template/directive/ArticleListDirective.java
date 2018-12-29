package com.korres.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.entity.Tag;
import com.korres.service.ArticleCategoryService;
import com.korres.service.ArticleService;
import com.korres.service.TagService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("articleListDirective")
public class ArticleListDirective extends BaseDirective {
	private static final String ARTICLE_CATEGORY_ID = "articleCategoryId";
	private static final String TAG_IDS = "tagIds";
	private static final String ARTICLES = "articles";

	@Resource(name = "articleServiceImpl")
	private ArticleService articleService;

	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Long articleCategoryId = (Long) FreemarkerUtils.getParameter(
				ARTICLE_CATEGORY_ID, Long.class, params);
		Long[] arrayOfLong = (Long[]) FreemarkerUtils.getParameter(TAG_IDS,
				Long[].class, params);
		ArticleCategory localArticleCategory = (ArticleCategory) this.articleCategoryService
				.find(articleCategoryId);
		List<Tag> localList1 = this.tagService.findList(arrayOfLong);

		List<Article> lia;
		if (((articleCategoryId != null) && (localArticleCategory == null))
				|| ((arrayOfLong != null) && (localList1.isEmpty()))) {
			lia = new ArrayList<Article>();
		} else {
			boolean bool = useCache(env, params);
			String cacheRegion = cacheRegion(env, params);
			Integer count = this.getCount(params);
			List<Filter> lif = getFilters(params, Article.class, new String[0]);
			List<Order> lio = getOrders(params, new String[0]);
			if (bool) {
				lia = this.articleService.findList(localArticleCategory,
						localList1, count, lif, lio, cacheRegion);
			} else {
				lia = this.articleService.findList(localArticleCategory,
						localList1, count, lif, lio);
			}
		}

		setVariables(ARTICLES, lia, env, body);
	}
}