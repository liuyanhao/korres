package com.korres.service.impl;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.korres.dao.ArticleDao;
import com.korres.dao.ProductDao;
import com.korres.entity.Article;
import com.korres.entity.Product;
import com.korres.entity.Product.ProductOrderType;
import com.korres.service.SearchService;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;

@Service("searchServiceImpl")
@Transactional
public class SearchServiceImpl implements SearchService {
	private static final float MINIMUM_SIMILARITY = 0.5F;

	@PersistenceContext
	protected EntityManager entityManager;

	@Resource(name = "articleDaoImpl")
	private ArticleDao articleDao;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	public void index() {
		index(Article.class);
		index(Product.class);
	}

	public void index(Class<?> type) {
		FullTextEntityManager fullTextEntityManager = Search
				.getFullTextEntityManager(this.entityManager);
		if (type == Article.class)
			for (int i = 0; i < this.articleDao.count(new Filter[0]); i += 20) {
				List<Article> lia = this.articleDao.findList(
						Integer.valueOf(i), Integer.valueOf(20), null, null);
				Iterator<Article> iterator = lia.iterator();
				while (iterator.hasNext()) {
					Article article = iterator.next();
					fullTextEntityManager.index(article);
				}

				fullTextEntityManager.flushToIndexes();
				fullTextEntityManager.clear();
				this.articleDao.clear();
			}
		else if (type == Product.class)
			for (int i = 0; i < this.productDao.count(new Filter[0]); i += 20) {
				List<Product> lip = this.productDao.findList(
						Integer.valueOf(i), Integer.valueOf(20), null, null);
				Iterator<Product> iterator = lip.iterator();
				while (iterator.hasNext()) {
					Product product = iterator.next();
					fullTextEntityManager.index(product);
				}

				fullTextEntityManager.flushToIndexes();
				fullTextEntityManager.clear();
				this.productDao.clear();
			}
	}

	public void index(Article article) {
		if (article != null) {
			FullTextEntityManager fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			fullTextEntityManager.index(article);
		}
	}

	public void index(Product product) {
		if (product != null) {
			FullTextEntityManager fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			fullTextEntityManager.index(product);
		}
	}

	public void purge() {
		purge(Article.class);
		purge(Product.class);
	}

	public void purge(Class<?> type) {
		FullTextEntityManager fullTextEntityManager = Search
				.getFullTextEntityManager(this.entityManager);
		if (type == Article.class) {
			fullTextEntityManager.purgeAll(Article.class);
		} else if (type == Product.class) {
			fullTextEntityManager.purgeAll(Product.class);
		}
	}

	public void purge(Article article) {
		if (article != null) {
			FullTextEntityManager fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			fullTextEntityManager.purge(Article.class, article.getId());
		}
	}

	public void purge(Product product) {
		if (product != null) {
			FullTextEntityManager fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			fullTextEntityManager.purge(Product.class, product.getId());
		}
	}

	@Transactional(readOnly = true)
	public Page<Article> search(String keyword, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page();
		}

		if (pageable == null) {
			pageable = new Pageable();
		}

		try {
			String str = QueryParser.escape(keyword);
			QueryParser queryParser = new QueryParser(Version.LUCENE_35,
					"title", new IKAnalyzer());
			queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query localQuery = queryParser.parse(str);
			FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("title", str), MINIMUM_SIMILARITY);
			TermQuery termQuery1 = new TermQuery(new Term("content", str));
			TermQuery termQuery2 = new TermQuery(new Term("isPublication",
					"true"));
			BooleanQuery booleanQuery1 = new BooleanQuery();
			BooleanQuery booleanQuery2 = new BooleanQuery();
			booleanQuery1.add(localQuery, BooleanClause.Occur.SHOULD);
			booleanQuery1.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
			booleanQuery1.add(termQuery1, BooleanClause.Occur.SHOULD);
			booleanQuery2.add(termQuery2, BooleanClause.Occur.MUST);
			booleanQuery2.add(booleanQuery1, BooleanClause.Occur.MUST);
			FullTextEntityManager fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			FullTextQuery fullTextQuery = fullTextEntityManager
					.createFullTextQuery(booleanQuery2,
							new Class[] { Article.class });
			fullTextQuery.setSort(new Sort(new SortField[] {
					new SortField("isTop", 3, true), new SortField(null, 0),
					new SortField("createDate", 6, true) }));
			fullTextQuery.setFirstResult((pageable.getPageNumber() - 1)
					* pageable.getPageSize());
			fullTextQuery.setMaxResults(pageable.getPageSize());
			return new Page(fullTextQuery.getResultList(), fullTextQuery
					.getResultSize(), pageable);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Page();
	}

	@Transactional(readOnly = true)
	public Page<Product> search(String keyword, BigDecimal startPrice,
			BigDecimal endPrice, ProductOrderType orderType, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page();
		}

		if (pageable == null) {
			pageable = new Pageable();
		}

		try {
			String str = QueryParser.escape(keyword);
			TermQuery termQuery1 = new TermQuery(new Term("sn", str));
			Query query1 = new QueryParser(Version.LUCENE_35, "keyword",
					new IKAnalyzer()).parse(str);
			QueryParser queryParser = new QueryParser(Version.LUCENE_35,
					"name", new IKAnalyzer());
			queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query query2 = queryParser.parse(str);
			FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("name", str), MINIMUM_SIMILARITY);
			TermQuery termQuery2 = new TermQuery(new Term("introduction",
					str));
			TermQuery termQuery3 = new TermQuery(new Term("isMarketable",
					"true"));
			TermQuery termQuery4 = new TermQuery(
					new Term("isList", "true"));
			TermQuery termQuery5 = new TermQuery(new Term("isGift",
					"false"));
			BooleanQuery booleanQuery1 = new BooleanQuery();
			BooleanQuery booleanQuery2 = new BooleanQuery();
			booleanQuery1.add(termQuery1, BooleanClause.Occur.SHOULD);
			booleanQuery1.add(query1, BooleanClause.Occur.SHOULD);
			booleanQuery1.add(query2, BooleanClause.Occur.SHOULD);
			booleanQuery1.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
			booleanQuery1.add(termQuery2, BooleanClause.Occur.SHOULD);
			booleanQuery2.add(termQuery3, BooleanClause.Occur.MUST);
			booleanQuery2.add(termQuery4, BooleanClause.Occur.MUST);
			booleanQuery2.add(termQuery5, BooleanClause.Occur.MUST);
			booleanQuery2.add(booleanQuery1, BooleanClause.Occur.MUST);
			if ((startPrice != null) && (endPrice != null)
					&& (startPrice.compareTo(endPrice) > 0)) {
				BigDecimal localObject = startPrice;
				startPrice = endPrice;
				endPrice = localObject;
			}

			if ((startPrice != null)
					&& (startPrice.compareTo(new BigDecimal(0)) >= 0)
					&& (endPrice != null)
					&& (endPrice.compareTo(new BigDecimal(0)) >= 0)) {
				Query query = NumericRangeQuery.newDoubleRange("price",
						Double.valueOf(startPrice.doubleValue()), Double
								.valueOf(endPrice.doubleValue()), true, true);
				booleanQuery2.add(query, BooleanClause.Occur.MUST);
			} else if ((startPrice != null)
					&& (startPrice.compareTo(new BigDecimal(0)) >= 0)) {
				Query query = NumericRangeQuery.newDoubleRange("price",
						Double.valueOf(startPrice.doubleValue()), null, true,
						false);
				booleanQuery2.add(query, BooleanClause.Occur.MUST);
			} else if ((endPrice != null)
					&& (endPrice.compareTo(new BigDecimal(0)) >= 0)) {
				Query query = NumericRangeQuery.newDoubleRange("price",
						null, Double.valueOf(endPrice.doubleValue()), false,
						true);
				booleanQuery2.add(query, BooleanClause.Occur.MUST);
			}
			
			FullTextEntityManager fullTextEntityManager = Search
					.getFullTextEntityManager(this.entityManager);
			FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(booleanQuery2,
							new Class[] { Product.class });
			SortField[] sortField = null;
			if (orderType == ProductOrderType.priceAsc) {
				sortField = new SortField[] {
						new SortField("price", 7, false),
						new SortField("createDate", 6, true) };
			} else if (orderType == ProductOrderType.priceDesc) {
				sortField = new SortField[] {
						new SortField("price", 7, true),
						new SortField("createDate", 6, true) };
			} else if (orderType == ProductOrderType.salesDesc) {
				sortField = new SortField[] {
						new SortField("sales", 4, true),
						new SortField("createDate", 6, true) };
			} else if (orderType == ProductOrderType.scoreDesc) {
				sortField = new SortField[] {
						new SortField("score", 4, true),
						new SortField("createDate", 6, true) };
			} else if (orderType == ProductOrderType.dateDesc) {
				sortField = new SortField[] { new SortField(
						"createDate", 6, true) };
			} else {
				sortField = new SortField[] {
						new SortField("isTop", 3, true),
						new SortField(null, 0),
						new SortField("modifyDate", 6, true) };
			}

			fullTextQuery.setSort(new Sort(sortField));
			fullTextQuery.setFirstResult((pageable.getPageNumber() - 1)
					* pageable.getPageSize());
			fullTextQuery.setMaxResults(pageable.getPageSize());
			return new Page(fullTextQuery.getResultList(), fullTextQuery
					.getResultSize(), pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Page();
	}
}