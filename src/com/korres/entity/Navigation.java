package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Navigation.java
 * 功能说明：导航栏实体类
 * 创建日期：2018-08-28 下午03:11:54
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_navigation")
public class Navigation extends OrderEntity {
	private static final long serialVersionUID = -7635757647887646795L;
	private String name;
	private NavigationPosition position;
	private String url;
	private Boolean isBlankTarget;

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
	public NavigationPosition getPosition() {
		return this.position;
	}

	public void setPosition(NavigationPosition position) {
		this.position = position;
	}

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsBlankTarget() {
		return this.isBlankTarget;
	}

	public void setIsBlankTarget(Boolean isBlankTarget) {
		this.isBlankTarget = isBlankTarget;
	}

	public enum NavigationPosition {
		top, middle, bottom;
	}
}