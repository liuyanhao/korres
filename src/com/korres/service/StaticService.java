package com.korres.service;

import java.util.Map;
import com.korres.entity.Article;
import com.korres.entity.Product;

public abstract interface StaticService {
	public abstract int build(String paramString1, String paramString2,
			Map<String, Object> paramMap);

	public abstract int build(String paramString1, String paramString2);

	public abstract int build(Article paramArticle);

	public abstract int build(Product paramProduct);

	public abstract int buildIndex();

	public abstract int buildSitemap();

	public abstract int buildOther();

	public abstract int buildAll();

	public abstract int delete(String paramString);

	public abstract int delete(Article paramArticle);

	public abstract int delete(Product paramProduct);

	public abstract int deleteIndex();

	public abstract int deleteOther();
}