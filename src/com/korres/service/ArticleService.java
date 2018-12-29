package com.korres.service;

import java.util.Date;
import java.util.List;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Article;
import com.korres.entity.ArticleCategory;
import com.korres.entity.Tag;

public abstract interface ArticleService extends BaseService<Article, Long> {
	public abstract List<Article> findList(
			ArticleCategory paramArticleCategory, List<Tag> paramList,
			Integer paramInteger, List<Filter> paramList1,
			List<Order> paramList2);

	public abstract List<Article> findList(
			ArticleCategory paramArticleCategory, List<Tag> paramList,
			Integer paramInteger, List<Filter> paramList1,
			List<Order> paramList2, String paramString);

	public abstract List<Article> findList(
			ArticleCategory paramArticleCategory, Date paramDate1,
			Date paramDate2, Integer paramInteger1, Integer paramInteger2);

	public abstract Page<Article> findPage(
			ArticleCategory paramArticleCategory, List<Tag> paramList,
			Pageable paramPageable);

	public abstract long viewHits(Long paramLong);
}