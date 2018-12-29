package com.korres.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：ArticleCategory.java
 * 功能说明：文章类别实体类
 * 创建日期：2013-8-20 下午04:35:48
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Entity
@Table(name = "xx_article_category")
public class ArticleCategory extends OrderEntity {
	private static final long serialVersionUID = -5132652107151648662L;
	public static final String SEPARATOR = ",";
	private static final String PATH = "/article/list/";
	private static final String SUFFIX = ".jhtml";
	private String name;
	private String seoTitle;
	private String seoKeywords;
	private String seoDescription;
	private String treePath;
	private Integer grade;
	private ArticleCategory parent;
	private Set<ArticleCategory> children = new HashSet<ArticleCategory>();
	private Set<Article> articles = new HashSet<Article>();

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
		this.seoKeywords = seoKeywords;
	}

	@Length(max = 200)
	public String getSeoDescription() {
		return this.seoDescription;
	}

	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	@Column(nullable = false)
	public String getTreePath() {
		return this.treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	@Column(nullable = false)
	public Integer getGrade() {
		return this.grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public ArticleCategory getParent() {
		return this.parent;
	}

	public void setParent(ArticleCategory parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	@OrderBy("order asc")
	public Set<ArticleCategory> getChildren() {
		return this.children;
	}

	public void setChildren(Set<ArticleCategory> children) {
		this.children = children;
	}

	@OneToMany(mappedBy = "articleCategory", fetch = FetchType.LAZY)
	public Set<Article> getArticles() {
		return this.articles;
	}

	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}

	@Transient
	public List<Long> getTreePaths() {
		List<Long> litp = new ArrayList<Long>();
		String[] tps = StringUtils.split(getTreePath(), SEPARATOR);
		if (tps != null) {
			for (String str : tps) {
				litp.add(Long.valueOf(str));
			}
		}

		return litp;
	}

	@Transient
	public String getPath() {
		if (getId() != null) {
			return PATH + getId() + SUFFIX;
		}

		return null;
	}
}