package com.korres.controller.shop;

import javax.annotation.Resource;
import com.korres.entity.ArticleCategory;
import com.korres.service.ArticleCategoryService;
import com.korres.service.ArticleService;
import com.korres.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Pageable;
import com.korres.ResourceNotFoundException;

@Controller("shopArticleController")
@RequestMapping({ "/article" })
public class ArticleController extends BaseController {
	private static final int PAGE_SIZE = 20;

	@Resource(name = "articleServiceImpl")
	private ArticleService articleService;

	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;

	@Resource(name = "searchServiceImpl")
	private SearchService searchService;

	@RequestMapping(value = { "/list/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(@PathVariable Long id, Integer pageNumber, ModelMap model) {
		ArticleCategory articleCategory = (ArticleCategory) this.articleCategoryService
				.find(id);
		if (articleCategory == null) {
			throw new ResourceNotFoundException();
		}

		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("articleCategory", articleCategory);
		model.addAttribute("page",
				this.articleService.findPage(articleCategory, null, pageable));

		return "/shop/article/list";
	}

	@RequestMapping(value = { "/search" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String search(String keyword, Integer pageNumber, ModelMap model) {
		if (StringUtils.isEmpty(keyword)) {
			return SHOP_COMMON_ERROR;
		}

		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("articleKeyword", keyword);
		model.addAttribute("page", this.searchService.search(keyword, pageable));

		return "shop/article/search";
	}

	@RequestMapping(value = { "/hits/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Long hits(@PathVariable Long id) {
		return Long.valueOf(this.articleService.viewHits(id));
	}
}