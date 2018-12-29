package com.korres.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：AdPosition.java
 * 功能说明：广告位置实体类
 * 创建日期：2013-8-20 下午02:54:02
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_ad_position")
public class AdPosition extends BaseEntity {
	private static final long serialVersionUID = -7849848867030199578L;
	private String name;
	private Integer width;
	private Integer height;
	private String description;
	private String template;
	private Set<Ad> ads = new HashSet<Ad>();

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

	@Length(max = 200)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotEmpty
	@Lob
	@Column(nullable = false)
	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@OneToMany(mappedBy = "adPosition", fetch = FetchType.EAGER, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("order asc")
	public Set<Ad> getAds() {
		return this.ads;
	}

	public void setAds(Set<Ad> ads) {
		this.ads = ads;
	}
}