package com.korres.service;

import java.util.List;
import com.korres.entity.ArticleCategory;

public abstract interface ArticleCategoryService extends
		BaseService<ArticleCategory, Long> {
	public abstract List<ArticleCategory> findRoots();

	public abstract List<ArticleCategory> findRoots(Integer paramInteger);

	public abstract List<ArticleCategory> findRoots(Integer paramInteger,
			String paramString);

	public abstract List<ArticleCategory> findParents(
			ArticleCategory paramArticleCategory);

	public abstract List<ArticleCategory> findParents(
			ArticleCategory paramArticleCategory, Integer paramInteger);

	public abstract List<ArticleCategory> findParents(
			ArticleCategory paramArticleCategory, Integer paramInteger,
			String paramString);

	public abstract List<ArticleCategory> findTree();

	public abstract List<ArticleCategory> findChildren(
			ArticleCategory paramArticleCategory);

	public abstract List<ArticleCategory> findChildren(
			ArticleCategory paramArticleCategory, Integer paramInteger);

	public abstract List<ArticleCategory> findChildren(
			ArticleCategory paramArticleCategory, Integer paramInteger,
			String paramString);
}