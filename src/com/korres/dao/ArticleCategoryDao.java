package com.korres.dao;

import java.util.List;
import com.korres.entity.ArticleCategory;

public abstract interface ArticleCategoryDao extends
		BaseDao<ArticleCategory, Long> {
	public abstract List<ArticleCategory> findRoots(Integer paramInteger);

	public abstract List<ArticleCategory> findParents(
			ArticleCategory paramArticleCategory, Integer paramInteger);

	public abstract List<ArticleCategory> findChildren(
			ArticleCategory paramArticleCategory, Integer paramInteger);
}