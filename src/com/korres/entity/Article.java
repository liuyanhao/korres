package com.korres.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.dom4j.io.SAXReader;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.core.io.ClassPathResource;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.korres.CommonAttributes;
import com.korres.util.FreemarkerUtils;

/*
 * 类名：Article.java
 * 功能说明：文章实体类
 * 创建日期：2018-08-20 下午03:10:47
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Indexed
@Entity
@Table(name = "xx_article")
public class Article extends BaseEntity {
	private static final long serialVersionUID = 1475773294701585482L;
	public static final String HITS_CACHE_NAME = "articleHits";
	public static final int HITS_CACHE_INTERVAL = 600000;
	private static final int MAX_PAGE_CONTENTS = 800;
	private static final String PAGE_BREAK = "<hr class=\"pageBreak\" />";
	private static final Pattern IIIlllII = Pattern.compile("[,;\\.!?，；。！？]");
	private static String staticPath;
	private String title;
	private String author;
	private String content;
	private String seoTitle;
	private String seoKeywords;
	private String seoDescription;
	private Boolean isPublication;
	private Boolean isTop;
	private Long hits;
	private Integer pageNumber;
	private ArticleCategory articleCategory;
	private Set<Tag> tags = new HashSet<Tag>();

	static {
		try {
			File localFile = new ClassPathResource(
					CommonAttributes.KORRES_XML_PATH).getFile();
			org.dom4j.Document localDocument = new SAXReader().read(localFile);
			org.dom4j.Element localElement = (org.dom4j.Element) localDocument
					.selectSingleNode("/korres/template[@id='articleContent']");
			staticPath = localElement.attributeValue("staticPath");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Field(store = Store.YES, index = Index.TOKENIZED, analyzer = @Analyzer(impl = IKAnalyzer.class))
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Field(store = Store.YES, index = Index.NO)
	@Length(max = 200)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Field(store = Store.YES, index = Index.TOKENIZED, analyzer = @Analyzer(impl = IKAnalyzer.class))
	@Lob
	public String getContent() {
		if (this.pageNumber != null) {
			String[] contents = getPageContents();
			if (this.pageNumber.intValue() < 1) {
				this.pageNumber = Integer.valueOf(1);
			}

			if (this.pageNumber.intValue() > contents.length) {
				this.pageNumber = Integer.valueOf(contents.length);
			}

			return contents[(this.pageNumber.intValue() - 1)];
		}

		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Length(max = 200)
	public String getSeoTitle() {
		return this.seoTitle;
	}

	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	@Length(max = 200)
	public String getSeoKeywords() {
		return this.seoKeywords;
	}

	public void setSeoKeywords(String seoKeywords) {
		if (seoKeywords != null) {
			seoKeywords = seoKeywords.replaceAll("[,\\s]*,[,\\s]*", ",")
					.replaceAll("^,|,$", "");
		}

		this.seoKeywords = seoKeywords;
	}

	@Length(max = 200)
	public String getSeoDescription() {
		return this.seoDescription;
	}

	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@NotNull
	@Column(nullable = false)
	public Boolean getIsPublication() {
		return this.isPublication;
	}

	public void setIsPublication(Boolean isPublication) {
		this.isPublication = isPublication;
	}

	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@NotNull
	@Column(nullable = false)
	public Boolean getIsTop() {
		return this.isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	@Column(nullable = false)
	public Long getHits() {
		return this.hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	@Transient
	public Integer getPageNumber() {
		return this.pageNumber;
	}

	@Transient
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public ArticleCategory getArticleCategory() {
		return this.articleCategory;
	}

	public void setArticleCategory(ArticleCategory articleCategory) {
		this.articleCategory = articleCategory;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_article_tag")
	@OrderBy("order asc")
	public Set<Tag> getTags() {
		return this.tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	@Transient
	public String getPath() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", getId());
		map.put("createDate", getCreateDate());
		map.put("modifyDate", getModifyDate());
		map.put("title", getTitle());
		map.put("seoTitle", getSeoTitle());
		map.put("seoKeywords", getSeoKeywords());
		map.put("seoDescription", getSeoDescription());
		map.put("pageNumber", getPageNumber());
		map.put("articleCategory", getArticleCategory());
		return FreemarkerUtils.process(staticPath, map);
	}

	@Transient
	public String getText() {
		if (getContent() != null) {
			return Jsoup.parse(getContent()).text();
		}

		return null;
	}

	@Transient
	public String[] getPageContents() {
		if (StringUtils.isEmpty(this.content)) {
			return new String[] { "" };
		}

		if (this.content.contains(PAGE_BREAK)) {
			return this.content.split(PAGE_BREAK);
		}

		List<String> lic = new ArrayList<String>();
		org.jsoup.nodes.Document document = Jsoup.parse(this.content);
		List<Node> linode = document.body().childNodes();
		if (linode != null) {
			int i = 0;
			StringBuffer stringBuffer = new StringBuffer();
			Iterator<Node> iterator = linode.iterator();
			while (iterator.hasNext()) {
				Node node = iterator.next();
				if ((node instanceof org.jsoup.nodes.Element)) {
					org.jsoup.nodes.Element element = (org.jsoup.nodes.Element) node;
					stringBuffer.append(element.outerHtml());
					i += element.text().length();
					if (i >= MAX_PAGE_CONTENTS) {
						lic.add(stringBuffer.toString());
						i = 0;
						stringBuffer.setLength(0);
					}
				} else if ((node instanceof TextNode)) {
					TextNode textNode = (TextNode) node;
					String str1 = textNode.text();
					String[] arrayOfString1 = IIIlllII.split(str1);
					Matcher localMatcher = IIIlllII.matcher(str1);
					for (String str2 : arrayOfString1) {
						if (localMatcher.find())
							str2 = str2 + localMatcher.group();
						stringBuffer.append(str2);
						i += str2.length();
						if (i >= MAX_PAGE_CONTENTS) {
							lic.add(stringBuffer.toString());
							i = 0;
							stringBuffer.setLength(0);
						}
					}
				}
			}

			String content = stringBuffer.toString();
			if (StringUtils.isNotEmpty(content)) {
				lic.add(content);
			}
		}

		return (String[]) lic.toArray(new String[lic.size()]);
	}

	@Transient
	public int getTotalPages() {
		return getPageContents().length;
	}
}