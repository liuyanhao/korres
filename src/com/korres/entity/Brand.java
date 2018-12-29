package com.korres.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Brand.java
 * 功能说明：品牌实体类
 * 创建日期：2013-8-20 下午04:38:33
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_brand")
public class Brand extends OrderEntity {
	private static final long serialVersionUID = -6109590619136943215L;
	private static final String PATH = "/brand/content/";
	private static final String SUFFIX = ".jhtml";
	private String name;
	private BrandType type;
	private String logo;
	private String url;
	private String introduction;
	private Set<Product> products = new HashSet<Product>();
	private Set<ProductCategory> productCategories = new HashSet<ProductCategory>();
	private Set<Promotion> promotions = new HashSet<Promotion>();

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Column(nullable = false)
	public BrandType getType() {
		return this.type;
	}

	public void setType(BrandType type) {
		this.type = type;
	}

	@Length(max = 200)
	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Length(max = 200)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Lob
	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@ManyToMany(mappedBy = "brands", fetch = FetchType.LAZY)
	@OrderBy("order asc")
	public Set<ProductCategory> getProductCategories() {
		return this.productCategories;
	}

	public void setProductCategories(Set<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	@ManyToMany(mappedBy = "brands", fetch = FetchType.LAZY)
	public Set<Promotion> getPromotions() {
		return this.promotions;
	}

	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	@Transient
	public String getPath() {
		if (getId() != null) {
			return PATH + getId() + SUFFIX;
		}

		return null;
	}

	@PreRemove
	public void preRemove() {
		Set<Product> setProduct = getProducts();
		if (setProduct != null) {
			Iterator<Product> iterator = setProduct.iterator();
			while (iterator.hasNext()) {
				Product product = iterator.next();
				product.setBrand(null);
			}
		}

		Set<ProductCategory> setProductCategory = getProductCategories();
		if (setProductCategory != null) {
			Iterator<ProductCategory> iterator = setProductCategory.iterator();
			while (iterator.hasNext()) {
				ProductCategory productCategory = iterator.next();
				productCategory.getBrands().remove(this);
			}
		}

		Set<Promotion> setPromotion = getPromotions();
		if (setPromotion != null) {
			Iterator<Promotion> iterator = setPromotion.iterator();
			while (iterator.hasNext()) {
				Promotion promotion = iterator.next();
				promotion.getBrands().remove(this);
			}
		}
	}

	public static enum BrandType {
		text, image;
	}
}