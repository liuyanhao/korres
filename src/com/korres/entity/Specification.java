package com.korres.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Specification.java
 * 功能说明：
 * 创建日期：2013-8-28 下午03:39:44
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_specification")
public class Specification extends OrderEntity {
	private static final long serialVersionUID = -6346775052811140926L;
	private String name;
	private SpecificationType type;
	private String memo;
	private List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();
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

	@NotNull
	@Column(nullable = false)
	public SpecificationType getType() {
		return this.type;
	}

	public void setType(SpecificationType type) {
		this.type = type;
	}

	@Length(max = 200)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Valid
	@NotEmpty
	@OneToMany(mappedBy = "specification", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, orphanRemoval = true)
	@OrderBy("order asc")
	public List<SpecificationValue> getSpecificationValues() {
		return this.specificationValues;
	}

	public void setSpecificationValues(
			List<SpecificationValue> specificationValues) {
		this.specificationValues = specificationValues;
	}

	@ManyToMany(mappedBy = "specifications", fetch = FetchType.LAZY)
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public enum SpecificationType {
		text, image;
	}
}