package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：ProductNotify.java
 * 功能说明：产品到货通知
 * 创建日期：2013-8-28 下午03:23:57
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_product_notify")
public class ProductNotify extends BaseEntity {
	private static final long serialVersionUID = 3192904068727393421L;
	private String email;
	private Boolean hasSent;
	private Member member;
	private Product product;

	@NotEmpty
	@Email
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(nullable = false)
	public Boolean getHasSent() {
		return this.hasSent;
	}

	public void setHasSent(Boolean hasSent) {
		this.hasSent = hasSent;
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
}