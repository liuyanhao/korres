package com.korres.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：MemberAttribute.java
 * 功能说明：会员熟悉实体类
 * 创建日期：2018-08-28 下午03:08:57
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_member_attribute")
public class MemberAttribute extends OrderEntity {
	private static final long serialVersionUID = 4513705276569738136L;
	private String name;
	private MemberAttributeType type;
	private Boolean isEnabled;
	private Boolean isRequired;
	private Integer propertyIndex;
	private List<String> options = new ArrayList<String>();

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
	public MemberAttributeType getType() {
		return this.type;
	}

	public void setType(MemberAttributeType type) {
		this.type = type;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsRequired() {
		return this.isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Column(updatable = false)
	public Integer getPropertyIndex() {
		return this.propertyIndex;
	}

	public void setPropertyIndex(Integer propertyIndex) {
		this.propertyIndex = propertyIndex;
	}

	@ElementCollection
	@CollectionTable(name = "xx_member_attribute_option")
	public List<String> getOptions() {
		return this.options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public enum MemberAttributeType {
		name, gender, birth, area, address, zipCode, phone, mobile, text, select, checkbox;
	}
}