package com.korres.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Attribute.java
 * 功能说明：属性实体类
 * 创建日期：2018-08-20 下午04:36:11
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_attribute")
public class Attribute extends OrderEntity {
	private static final long serialVersionUID = 2447794131117928367L;
	private String name;
	private Integer propertyIndex;
	private ProductCategory productCategory;
	private List<String> options = new ArrayList<String>();

	@JsonProperty
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false, updatable = false)
	public Integer getPropertyIndex() {
		return this.propertyIndex;
	}

	public void setPropertyIndex(Integer propertyIndex) {
		this.propertyIndex = propertyIndex;
	}

	@NotNull(groups = { BaseEntitySave.class })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public ProductCategory getProductCategory() {
		return this.productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	@JsonProperty
	@NotEmpty
	@ElementCollection
	@CollectionTable(name = "xx_attribute_option")
	public List<String> getOptions() {
		return this.options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}