package com.korres.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Tag.java
 * 功能说明：标签实体类
 * 创建日期：2018-08-28 下午03:40:29
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_tag")
public class Tag extends OrderEntity {
	private static final long serialVersionUID = -2735037966597250149L;
	private String name;
	private TagType type;
	private String icon;
	private String memo;
	private Set<Article> articles = new HashSet<Article>();
	private Set<Product> products = new HashSet<Product>();

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull(groups = { BaseEntitySave.class })
	@Column(nullable = false, updatable = false)
	public TagType getType() {
		return this.type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	@Length(max = 200)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Length(max = 200)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<Article> getArticles() {
		return this.articles;
	}

	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}

	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@PreRemove
	public void preRemove() {
		Set<Article> seta = getArticles();
		if (seta != null) {
			Iterator<Article> iterator = seta.iterator();
			while (iterator.hasNext()) {
				Article article = iterator.next();
				article.getTags().remove(this);
			}
		}

		Set<Product> setp = getProducts();
		if (setp != null) {
			Iterator<Product> iterator = setp.iterator();
			while (iterator.hasNext()) {
				Product product = iterator.next();
				product.getTags().remove(this);
			}
		}
	}

	public enum TagType {
		article, product;
	}
}