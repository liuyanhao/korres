package com.korres.service;

import java.math.BigDecimal;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Article;
import com.korres.entity.Product;
import com.korres.entity.Product.ProductOrderType;

public abstract interface SearchService {
	public abstract void index();

	public abstract void index(Class<?> paramClass);

	public abstract void index(Article paramArticle);

	public abstract void index(Product paramProduct);

	public abstract void purge();

	public abstract void purge(Class<?> paramClass);

	public abstract void purge(Article paramArticle);

	public abstract void purge(Product paramProduct);

	public abstract Page<Article> search(String paramString,
			Pageable paramPageable);

	public abstract Page<Product> search(String paramString,
			BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2,
			ProductOrderType paramOrderType, Pageable paramPageable);
}