package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：FriendLink.java
 * 功能说明：友情链接实体类
 * 创建日期：2013-8-28 下午02:58:42
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_friend_link")
public class FriendLink extends OrderEntity {
	private static final long serialVersionUID = 3019642557500517628L;
	private String name;
	private FriendLinkType type;
	private String logo;
	private String url;

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
	public FriendLinkType getType() {
		return this.type;
	}

	public void setType(FriendLinkType type) {
		this.type = type;
	}

	@Length(max = 200)
	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
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

	public enum FriendLinkType {
		text, image;
	}
}