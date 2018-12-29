package com.korres.controller.admin;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.service.ArticleCategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;

@Controller("adminArticleCategoryController")
@RequestMapping({ "/admin/article_category" })
public class ArticleCategoryController extends BaseController {

	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("articleCategoryTree",
				this.articleCategoryService.findTree());
		return "/admin/article_category/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(ArticleCategory articleCategory, Long parentId,
			RedirectAttributes redirectAttributes) {
		articleCategory.setParent((ArticleCategory) this.articleCategoryService
				.find(parentId));

		if (!validator(articleCategory, new Class[0])) {
			return "/admin/common/error";
		}

		articleCategory.setTreePath(null);
		articleCategory.setGrade(null);
		articleCategory.setChildren(null);
		articleCategory.setArticles(null);
		this.articleCategoryService.save(articleCategory);

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		ArticleCategory localArticleCategory = (ArticleCategory) this.articleCategoryService
				.find(id);
		model.addAttribute("articleCategoryTree",
				this.articleCategoryService.findTree());
		model.addAttribute("articleCategory", localArticleCategory);
		model.addAttribute("children",
				this.articleCategoryService.findChildren(localArticleCategory));

		return "/admin/article_category/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(ArticleCategory articleCategory, Long parentId,
			RedirectAttributes redirectAttributes) {
		articleCategory.setParent((ArticleCategory) this.articleCategoryService
				.find(parentId));

		if (!validator(articleCategory, new Class[0])) {
			return "/admin/common/error";
		}

		if (articleCategory.getParent() != null) {
			ArticleCategory ac = articleCategory.getParent();

			if (articleCategory.equals(ac)) {
				return "/admin/common/error";
			}

			List<ArticleCategory> liac = this.articleCategoryService
					.findChildren(ac);

			if ((liac != null) && (liac.contains(ac))) {
				return "/admin/common/error";
			}
		}

		this.articleCategoryService.update(articleCategory, new String[] {
				"treePath", "grade", "children", "articles" });

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ModelMap model) {
		model.addAttribute("articleCategoryTree",
				this.articleCategoryService.findTree());
		
		return "/admin/article_category/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long id) {
		ArticleCategory articleCategory = (ArticleCategory) this.articleCategoryService
				.find(id);

		if (articleCategory == null) {
			return ADMIN_MESSAGE_ERROR;
		}

		Set<ArticleCategory> children = articleCategory.getChildren();
		if ((children != null) && (!children.isEmpty())) {
			return Message.error(
					"admin.articleCategory.deleteExistChildrenNotAllowed",
					new Object[0]);
		}

		Set<Article> article = articleCategory.getArticles();
		if ((article != null) && (!article.isEmpty())) {
			return Message.error(
					"admin.articleCategory.deleteExistArticleNotAllowed",
					new Object[0]);
		}

		this.articleCategoryService.delete(id);

		return ADMIN_MESSAGE_SUCCESS;
	}
}