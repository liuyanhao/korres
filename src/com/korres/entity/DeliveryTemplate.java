package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：DeliveryTemplate.java
 * 功能说明：物流模板实体类
 * 创建日期：2018-08-28 下午02:57:15
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_delivery_template")
public class DeliveryTemplate extends BaseEntity {
	private static final long serialVersionUID = -3711024981692804054L;
	private String name;
	private String content;
	private Integer width;
	private Integer height;
	private Integer offsetX;
	private Integer offsetY;
	private String background;
	private Boolean isDefault;
	private String memo;

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotEmpty
	@Lob
	@Column(nullable = false)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@NotNull
	@Min(1L)
	@Column(nullable = false)
	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@NotNull
	@Min(1L)
	@Column(nullable = false)
	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@NotNull
	@Column(nullable = false)
	public Integer getOffsetX() {
		return this.offsetX;
	}

	public void setOffsetX(Integer offsetX) {
		this.offsetX = offsetX;
	}

	@NotNull
	@Column(nullable = false)
	public Integer getOffsetY() {
		return this.offsetY;
	}

	public void setOffsetY(Integer offsetY) {
		this.offsetY = offsetY;
	}

	@Length(max = 200)
	public String getBackground() {
		return this.background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Length(max = 200)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}