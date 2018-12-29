package com.korres.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.korres.util.SettingUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.korres.Setting;

/*
 * 类名：Coupon.java
 * 功能说明：优惠券实体类
 * 创建日期：2013-8-28 下午02:53:24
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Entity
@Table(name = "xx_coupon")
public class Coupon extends BaseEntity {
	private static final long serialVersionUID = -7907808728349149722L;
	private String name;
	private String prefix;
	private Date beginDate;
	private Date endDate;
	private BigDecimal startPrice;
	private BigDecimal endPrice;
	private Boolean isEnabled;
	private Boolean isExchange;
	private Integer point;
	private CouponOperator priceOperator;
	private BigDecimal priceValue;
	private String introduction;
	private Set<CouponCode> couponCodes = new HashSet<CouponCode>();
	private Set<Promotion> promotions = new HashSet<Promotion>();
	private List<Order> orders = new ArrayList<Order>();

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
	@Length(max = 200)
	@Column(nullable = false)
	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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

	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getStartPrice() {
		return this.startPrice;
	}

	public void setStartPrice(BigDecimal startPrice) {
		this.startPrice = startPrice;
	}

	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getEndPrice() {
		return this.endPrice;
	}

	public void setEndPrice(BigDecimal endPrice) {
		this.endPrice = endPrice;
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
	public Boolean getIsExchange() {
		return this.isExchange;
	}

	public void setIsExchange(Boolean isExchange) {
		this.isExchange = isExchange;
	}

	@Min(0L)
	public Integer getPoint() {
		return this.point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	@NotNull
	@Column(nullable = false)
	public CouponOperator getPriceOperator() {
		return this.priceOperator;
	}

	public void setPriceOperator(CouponOperator priceOperator) {
		this.priceOperator = priceOperator;
	}

	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getPriceValue() {
		return this.priceValue;
	}

	public void setPriceValue(BigDecimal priceValue) {
		this.priceValue = priceValue;
	}

	@Lob
	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<CouponCode> getCouponCodes() {
		return this.couponCodes;
	}

	public void setCouponCodes(Set<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}

	@ManyToMany(mappedBy = "coupons", fetch = FetchType.LAZY)
	public Set<Promotion> getPromotions() {
		return this.promotions;
	}

	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	@ManyToMany(mappedBy = "coupons", fetch = FetchType.LAZY)
	public List<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	@Transient
	public boolean hasBegun() {
		return (getBeginDate() == null) || (new Date().after(getBeginDate()));
	}

	@Transient
	public boolean hasExpired() {
		return (getEndDate() != null) && (new Date().after(getEndDate()));
	}

	@Transient
	public BigDecimal calculatePrice(BigDecimal price) {
		if ((price != null) && (getPriceOperator() != null)
				&& (getPriceValue() != null)) {
			Setting setting = SettingUtils.get();
			BigDecimal amout = new BigDecimal(0);
			if (getPriceOperator() == CouponOperator.add) {
				amout = price.add(getPriceValue());
			} else if (getPriceOperator() == CouponOperator.subtract) {
				amout = price.subtract(getPriceValue());
			} else if (getPriceOperator() == CouponOperator.multiply) {
				amout = price.multiply(getPriceValue());
			} else {
				amout = price.divide(getPriceValue());
			}

			BigDecimal p = setting.setScale(amout);

			return p.compareTo(new BigDecimal(0)) > 0 ? p : new BigDecimal(0);
		}

		return price;
	}

	@PreRemove
	public void preRemove() {
		Set<Promotion> set = getPromotions();
		if (set != null) {
			Iterator<Promotion> iterator = set.iterator();
			while (iterator.hasNext()) {
				Promotion promotion = iterator.next();
				promotion.getCoupons().remove(this);
			}
		}

		List<Order> lio = getOrders();
		if (lio != null) {
			Iterator<Order> iterator = lio.iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				order.getCoupons().remove(this);
			}
		}
	}

	public enum CouponOperator {
		add, subtract, multiply, divide;
	}
}