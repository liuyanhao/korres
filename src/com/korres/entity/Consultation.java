package com.korres.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Consultation.java
 * 功能说明：咨询实体类
 * 创建日期：2018-08-28 下午02:42:05
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Entity
@Table(name = "xx_consultation")
public class Consultation extends BaseEntity {
	private static final long serialVersionUID = -3950317769006303385L;
	private static final String PATH = "/consultation/content/";
	private static final String SUFFIX = ".jhtml";
	private String content;
	private Boolean isShow;
	private String ip;
	private Member member;
	private Product product;
	private Consultation forConsultation;
	private Set<Consultation> replyConsultations = new HashSet<Consultation>();

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(nullable = false)
	public Boolean getIsShow() {
		return this.isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	@Column(nullable = false, updatable = false)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Consultation getForConsultation() {
		return this.forConsultation;
	}

	public void setForConsultation(Consultation forConsultation) {
		this.forConsultation = forConsultation;
	}

	@OneToMany(mappedBy = "forConsultation", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("createDate asc")
	public Set<Consultation> getReplyConsultations() {
		return this.replyConsultations;
	}

	public void setReplyConsultations(Set<Consultation> replyConsultations) {
		this.replyConsultations = replyConsultations;
	}

	@Transient
	public String getPath() {
		if ((getProduct() != null) && (getProduct().getId() != null)) {
			return PATH + getProduct().getId() + SUFFIX;
		}

		return null;
	}
}