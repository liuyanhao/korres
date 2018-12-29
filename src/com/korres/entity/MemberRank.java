package com.korres.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：MemberRank.java
 * 功能说明：会员等级实体类
 * 创建日期：2013-8-28 下午03:09:37
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_member_rank")
public class MemberRank extends BaseEntity {
	private static final long serialVersionUID = 3599029355500655209L;
	private String name;
	private Double scale;
	private BigDecimal amount;
	private Boolean isDefault;
	private Boolean isSpecial;
	private Set<Member> members = new HashSet<Member>();
	private Set<Promotion> promotions = new HashSet<Promotion>();

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, unique = true)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Min(0L)
	@Digits(integer = 3, fraction = 3)
	@Column(nullable = false, precision = 12, scale = 6)
	public Double getScale() {
		return this.scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(unique = true, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@NotNull
	@Column(nullable = false)
	public Boolean getIsSpecial() {
		return this.isSpecial;
	}

	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	@OneToMany(mappedBy = "memberRank", fetch = FetchType.LAZY)
	public Set<Member> getMembers() {
		return this.members;
	}

	public void setMembers(Set<Member> members) {
		this.members = members;
	}

	@ManyToMany(mappedBy = "memberRanks", fetch = FetchType.LAZY)
	public Set<Promotion> getPromotions() {
		return this.promotions;
	}

	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	@PreRemove
	public void preRemove() {
		Set<Promotion> set = getPromotions();
		if (set != null) {
			Iterator<Promotion> iterator = set.iterator();
			while (iterator.hasNext()) {
				Promotion promotion = iterator.next();
				promotion.getMemberRanks().remove(this);
			}
		}
	}
}