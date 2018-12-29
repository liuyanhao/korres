package com.korres.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import com.korres.dao.ArticleDao;
import com.korres.dao.BrandDao;
import com.korres.dao.ProductDao;
import com.korres.dao.PromotionDao;
import com.korres.entity.Article;
import com.korres.entity.Brand;
import com.korres.entity.Product;
import com.korres.entity.Promotion;
import com.korres.service.StaticService;
import com.korres.service.TemplateService;
import com.korres.util.FreemarkerUtils;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.korres.Filter;

@Service("staticServiceImpl")
public class StaticServiceImpl implements StaticService, ServletContextAware {
	private static final Integer IIIllIlI = Integer.valueOf(40000);
	private ServletContext servletContext;

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Resource(name = "templateServiceImpl")
	private TemplateService templateService;

	@Resource(name = "articleDaoImpl")
	private ArticleDao articleDao;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "brandDaoImpl")
	private BrandDao brandDao;

	@Resource(name = "promotionDaoImpl")
	private PromotionDao promotionDao;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Transactional(readOnly = true)
	public int build(String templatePath, String staticPath,
			Map<String, Object> model) {
		Assert.hasText(templatePath);
		Assert.hasText(staticPath);
		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bufferedWriter = null;
		try {
			freemarker.template.Template template = this.freeMarkerConfigurer
					.getConfiguration().getTemplate(templatePath);
			File file = new File(this.servletContext.getRealPath(staticPath));
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			out = new FileOutputStream(file);
			writer = new OutputStreamWriter(out, "UTF-8");
			bufferedWriter = new BufferedWriter(writer);
			template.process(model, bufferedWriter);
			bufferedWriter.flush();

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(out);
		}
		return 0;
	}

	@Transactional(readOnly = true)
	public int build(String templatePath, String staticPath) {
		return build(templatePath, staticPath, null);
	}

	@Transactional(readOnly = true)
	public int build(Article article) {
		Assert.notNull(article);
		delete(article);
		com.korres.Template template = this.templateService
				.get("articleContent");
		int i = 0;
		if (article.getIsPublication().booleanValue()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("article", article);
			for (int j = 1; j <= article.getTotalPages(); j++) {
				article.setPageNumber(Integer.valueOf(j));
				i += build(template.getTemplatePath(), article.getPath(), map);
			}

			article.setPageNumber(null);
		}

		return i;
	}

	@Transactional(readOnly = true)
	public int build(Product product) {
		Assert.notNull(product);
		delete(product);
		com.korres.Template template = this.templateService
				.get("productContent");
		int i = 0;
		if (product.getIsMarketable().booleanValue()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("product", product);
			i += build(template.getTemplatePath(), product.getPath(), map);
		}
		return i;
	}

	@Transactional(readOnly = true)
	public int buildIndex() {
		com.korres.Template template = this.templateService.get("index");
		return build(template.getTemplatePath(), template.getStaticPath());
	}

	@Transactional(readOnly = true)
	public int buildSitemap() {
		int i = 0;
		com.korres.Template template1 = this.templateService
				.get("sitemapIndex");
		com.korres.Template template2 = this.templateService.get("sitemap");
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<String> localArrayList = new ArrayList<String>();
		int j = 0;
		int k = 0;
		int m = 0;
		int n = IIIllIlI.intValue();
		while (true)
			try {
				map.put("index", Integer.valueOf(k));
				String templatePath = template2.getTemplatePath();
				String staticPath = FreemarkerUtils.process(template2
						.getStaticPath(), map);
				if (j == 0) {
					List<Article> lia = this.articleDao.findList(Integer
							.valueOf(m), Integer.valueOf(n), null, null);
					map.put("articles", lia);
					if (lia.size() < n) {
						j++;
						m = 0;
						n -= lia.size();
					} else {
						i += build(templatePath, staticPath, map);
						this.articleDao.clear();
						this.articleDao.flush();
						localArrayList.add(staticPath);
						map.clear();
						k++;
						m += lia.size();
						n = IIIllIlI.intValue();
					}
				} else if (j == 1) {
					List<Product> lip = this.productDao.findList(Integer
							.valueOf(m), Integer.valueOf(n), null, null);
					map.put("products", lip);
					if (lip.size() < n) {
						j++;
						m = 0;
						n -= lip.size();
					} else {
						i += build(templatePath, staticPath, map);
						this.productDao.clear();
						this.productDao.flush();
						localArrayList.add(staticPath);
						map.clear();
						k++;
						m += lip.size();
						n = IIIllIlI.intValue();
					}
				} else if (j == 2) {
					List<Brand> lib = this.brandDao.findList(
							Integer.valueOf(m), Integer.valueOf(n), null, null);
					map.put("brands", lib);
					if (lib.size() < n) {
						j++;
						m = 0;
						n -= lib.size();
					} else {
						i += build(templatePath, staticPath, map);
						this.brandDao.clear();
						this.brandDao.flush();
						localArrayList.add(staticPath);
						map.clear();
						k++;
						m += lib.size();
						n = IIIllIlI.intValue();
					}
				} else if (j == 3) {
					List<Promotion> lip = this.promotionDao.findList(Integer
							.valueOf(m), Integer.valueOf(n), null, null);
					map.put("promotions", lip);
					i += build(templatePath, staticPath, map);
					this.promotionDao.clear();
					this.promotionDao.flush();
					localArrayList.add(staticPath);
					if (lip.size() < n) {
						map.put("staticPaths", localArrayList);
						i += build(template1.getTemplatePath(), template1
								.getStaticPath(), map);
						break;
					}
					map.clear();
					k++;
					m += lip.size();
					n = IIIllIlI.intValue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		return i;
	}

	@Transactional(readOnly = true)
	public int buildOther() {
		int i = 0;
		com.korres.Template template1 = this.templateService
				.get("shopCommonJs");
		com.korres.Template template2 = this.templateService
				.get("adminCommonJs");
		i += build(template1.getTemplatePath(), template1.getStaticPath());
		i += build(template2.getTemplatePath(), template2.getStaticPath());
		return i;
	}

	@Transactional(readOnly = true)
	public int buildAll() {
		int i = 0;
		for (int j = 0; j < this.articleDao.count(new Filter[0]); j += 20) {
			List<Article> listarticle = this.articleDao.findList(Integer
					.valueOf(j), Integer.valueOf(20), null, null);
			Iterator<Article> iterator = listarticle.iterator();
			while (iterator.hasNext()) {
				Article article = iterator.next();
				i += build(article);
			}

			this.articleDao.clear();
		}

		for (int j = 0; j < this.productDao.count(new Filter[0]); j += 20) {
			List<Product> listproduct = this.productDao.findList(Integer
					.valueOf(j), Integer.valueOf(20), null, null);
			Iterator<Product> iterator = listproduct.iterator();
			while (iterator.hasNext()) {
				Product product = iterator.next();
				i += build(product);
			}

			this.productDao.clear();
		}

		buildIndex();
		buildSitemap();
		buildOther();

		return i;
	}

	@Transactional(readOnly = true)
	public int delete(String staticPath) {
		Assert.hasText(staticPath);
		File file = new File(this.servletContext.getRealPath(staticPath));
		if (file.exists()) {
			file.delete();
			return 1;
		}

		return 0;
	}

	@Transactional(readOnly = true)
	public int delete(Article article) {
		Assert.notNull(article);
		int i = 0;
		for (int j = 1; j <= article.getTotalPages() + 1000; j++) {
			article.setPageNumber(Integer.valueOf(j));
			int k = delete(article.getPath());
			if (k < 1) {
				break;
			}
			i += k;
		}

		article.setPageNumber(null);

		return i;
	}

	@Transactional(readOnly = true)
	public int delete(Product product) {
		Assert.notNull(product);
		return delete(product.getPath());
	}

	@Transactional(readOnly = true)
	public int deleteIndex() {
		com.korres.Template template = this.templateService.get("index");
		return delete(template.getStaticPath());
	}

	@Transactional(readOnly = true)
	public int deleteOther() {
		int i = 0;
		com.korres.Template template1 = this.templateService
				.get("shopCommonJs");
		com.korres.Template template2 = this.templateService
				.get("adminCommonJs");
		i += delete(template1.getStaticPath());
		i += delete(template2.getStaticPath());
		return i;
	}
}