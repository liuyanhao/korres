package com.korres.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.entity.Product;
import com.korres.entity.ProductCategory;
import com.korres.service.ArticleCategoryService;
import com.korres.service.ArticleService;
import com.korres.service.ProductCategoryService;
import com.korres.service.ProductService;
import com.korres.service.StaticService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("adminStaticController")
@RequestMapping( { "/admin/static" })
public class StaticController extends BaseController {

	@Resource(name = "articleServiceImpl")
	private ArticleService articleService;

	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@RequestMapping(value = { "/build" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String build(ModelMap model) {
		model.addAttribute("buildTypes", BuildType.values());
		model.addAttribute("defaultBeginDate", DateUtils
				.addDays(new Date(), -7));
		model.addAttribute("defaultEndDate", new Date());
		model.addAttribute("articleCategoryTree", this.articleCategoryService
				.findChildren(null, null));
		model.addAttribute("productCategoryTree", this.productCategoryService
				.findChildren(null, null));
		return "/admin/static/build";
	}

	@RequestMapping(value = { "/build" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> build(BuildType buildType,
			Long articleCategoryId, Long productCategoryId, Date beginDate,
			Date endDate, Integer first, Integer count) {
		long start = System.currentTimeMillis();
		
		if (beginDate != null) {
			Calendar calendar = DateUtils.toCalendar(beginDate);
			calendar.set(11, calendar.getActualMinimum(11));
			calendar.set(12, calendar.getActualMinimum(12));
			calendar.set(13, calendar.getActualMinimum(13));
			beginDate = calendar.getTime();
		}
		
		if (endDate != null) {
			Calendar calendar = DateUtils.toCalendar(endDate);
			calendar.set(11, calendar.getActualMaximum(11));
			calendar.set(12, calendar.getActualMaximum(12));
			calendar.set(13, calendar.getActualMaximum(13));
			endDate = calendar.getTime();
		}
		
		if ((first == null) || (first.intValue() < 0)){
			first = Integer.valueOf(0);
		}
		
		if ((count == null) || (count.intValue() <= 0)){
			count = Integer.valueOf(50);
		}
		
		int i = 0;
		boolean bool = true;
		if (buildType == BuildType.index) {
			i = this.staticService.buildIndex();
		} else {
			if (buildType == BuildType.article) {
				ArticleCategory articleCategory = this.articleCategoryService
						.find(articleCategoryId);
				List<Article> liArticle = this.articleService.findList(
						articleCategory, beginDate, endDate, first, count);
				Iterator<Article> iterator = liArticle.iterator();
				while (iterator.hasNext()) {
					Article article = iterator.next();
					i += this.staticService.build(article);
				}

				first = Integer.valueOf(first.intValue() + liArticle.size());
				if (liArticle.size() == count.intValue()) {
					bool = false;
				}
			} else if (buildType == BuildType.product) {
				ProductCategory productCategory = this.productCategoryService
						.find(productCategoryId);
				List<Product> liProduct = this.productService.findList(
						productCategory, beginDate, endDate, first, count);
				Iterator<Product> iterator = liProduct.iterator();
				while (iterator.hasNext()) {
					Product product = iterator.next();
					i += this.staticService.build(product);
				}
				first = Integer.valueOf(first.intValue() + liProduct.size());
				if (liProduct.size() == count.intValue())
					bool = false;
			} else if (buildType == BuildType.other) {
				i = this.staticService.buildOther();
			}
		}

		long end = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("first", first);
		map.put("buildCount", Integer.valueOf(i));
		map.put("buildTime", Long.valueOf(end - start));
		map.put("isCompleted", Boolean.valueOf(bool));

		return map;
	}

	public enum BuildType {
		index, article, product, other;
	}
}