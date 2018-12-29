package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;

/*
 * 类名：Seo.java
 * 功能说明：seo实体类
 * 创建日期：2018-08-28 下午03:34:23
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_seo")
public class Seo extends BaseEntity {
	private static final long serialVersionUID = -3503657242384822672L;
	private SeoType type;
	private String title;
	private String keywords;
	private String description;

	@Column(nullable = false, updatable = false, unique = true)
	public SeoType getType() {
		return this.type;
	}

	public void setType(SeoType type) {
		this.type = type;
	}

	@Length(max = 200)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Length(max = 200)
	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		if (keywords != null)
			keywords = keywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll(
					"^,|,$", "");
		this.keywords = keywords;
	}

	@Length(max = 200)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public enum SeoType {
		index, productList, productSearch, productContent, articleList, articleSearch, articleContent, brandList, brandContent;
	}
}