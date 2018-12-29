package com.korres.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Ad.java
 * 功能说明：广告管理实体类
 * 创建日期：2013-8-20 下午02:53:25
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_ad")
public class Ad extends OrderEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private AdType type;
	private String content;
	private String path;
	private Date beginDate;
	private Date endDate;
	private String url;
	private AdPosition adPosition;

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotNull
	@Column(nullable = false)
	public AdType getType() {
		return this.type;
	}

	public void setType(AdType type) {
		this.type = type;
	}

	@Lob
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Length(max = 200)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Length(max = 200)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public AdPosition getAdPosition() {
		return this.adPosition;
	}

	public void setAdPosition(AdPosition adPosition) {
		this.adPosition = adPosition;
	}

	@Transient
	public boolean hasBegun() {
		return (getBeginDate() == null) || (new Date().after(getBeginDate()));
	}

	@Transient
	public boolean hasEnded() {
		return (getEndDate() != null) && (new Date().after(getEndDate()));
	}

	public static enum AdType {
		text, image, flash;
	}
}