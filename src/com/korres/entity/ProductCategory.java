package com.korres.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：ProductCategory.java
 * 功能说明：产品类别实体类
 * 创建日期：2018-08-28 下午03:20:28
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_product_category")
public class ProductCategory extends OrderEntity {
	private static final long serialVersionUID = 5095521437302782717L;
	public static final String TREE_PATH_SEPARATOR = ",";
	private static final String PATH = "/product/list/";
	private static final String SUFFIX = ".jhtml";
	private String name;
	private String seoTitle;
	private String seoKeywords;
	private String seoDescription;
	private String treePath;
	private Integer grade;
	private ProductCategory parent;
	private Set<ProductCategory> children = new HashSet<ProductCategory>();
	private Set<Product> products = new HashSet<Product>();
	private Set<Brand> brands = new HashSet<Brand>();
	private Set<ParameterGroup> parameterGroups = new HashSet<ParameterGroup>();
	private Set<Attribute> attributes = new HashSet<Attribute>();
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
	public ProductCategory getParent() {
		return this.parent;
	}

	public void setParent(ProductCategory parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	@OrderBy("order asc")
	public Set<ProductCategory> getChildren() {
		return this.children;
	}

	public void setChildren(Set<ProductCategory> children) {
		this.children = children;
	}

	@OneToMany(mappedBy = "productCategory", fetch = FetchType.LAZY)
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_product_category_brand")
	@OrderBy("order asc")
	public Set<Brand> getBrands() {
		return this.brands;
	}

	public void setBrands(Set<Brand> brands) {
		this.brands = brands;
	}

	@OneToMany(mappedBy = "productCategory", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("order asc")
	public Set<ParameterGroup> getParameterGroups() {
		return this.parameterGroups;
	}

	public void setParameterGroups(Set<ParameterGroup> parameterGroups) {
		this.parameterGroups = parameterGroups;
	}

	@OneToMany(mappedBy = "productCategory", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("order asc")
	public Set<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	@ManyToMany(mappedBy = "productCategories", fetch = FetchType.LAZY)
	public Set<Promotion> getPromotions() {
		return this.promotions;
	}

	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	@Transient
	public List<Long> getTreePaths() {
		List<Long> treePaths = new ArrayList<Long>();
		String[] sptp = StringUtils.split(getTreePath(), ",");
		if (sptp != null) {
			for (String path : sptp) {
				treePaths.add(Long.valueOf(path));
			}
		}

		return treePaths;
	}

	@Transient
	public String getPath() {
		if (getId() != null)
			return PATH + getId() + SUFFIX;
		return null;
	}

	@PreRemove
	public void preRemove() {
		Set<Promotion> set = getPromotions();
		if (set != null) {
			Iterator<Promotion> iterator = set.iterator();
			while (iterator.hasNext()) {
				Promotion promotion = iterator.next();
				promotion.getProductCategories().remove(this);
			}
		}
	}
}