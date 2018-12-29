package com.korres.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
 * 类名：ShippingMethod.java
 * 功能说明：
 * 创建日期：2018-08-28 下午03:36:26
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_shipping_method")
public class ShippingMethod extends OrderEntity {
	private static final long serialVersionUID = 5873163245980853245L;
	private String name;
	private Integer firstWeight;
	private Integer continueWeight;
	private BigDecimal firstPrice;
	private BigDecimal continuePrice;
	private String icon;
	private String description;
	private DeliveryCorp defaultDeliveryCorp;
	private Set<PaymentMethod> paymentMethods = new HashSet<PaymentMethod>();
	private Set<Order> orders = new HashSet<Order>();

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
	@Min(0L)
	@Column(nullable = false)
	public Integer getFirstWeight() {
		return this.firstWeight;
	}

	public void setFirstWeight(Integer firstWeight) {
		this.firstWeight = firstWeight;
	}

	@NotNull
	@Min(1L)
	@Column(nullable = false)
	public Integer getContinueWeight() {
		return this.continueWeight;
	}

	public void setContinueWeight(Integer continueWeight) {
		this.continueWeight = continueWeight;
	}

	@NotNull
	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getFirstPrice() {
		return this.firstPrice;
	}

	public void setFirstPrice(BigDecimal firstPrice) {
		this.firstPrice = firstPrice;
	}

	@NotNull
	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getContinuePrice() {
		return this.continuePrice;
	}

	public void setContinuePrice(BigDecimal continuePrice) {
		this.continuePrice = continuePrice;
	}

	@Length(max = 200)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Lob
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public DeliveryCorp getDefaultDeliveryCorp() {
		return this.defaultDeliveryCorp;
	}

	public void setDefaultDeliveryCorp(DeliveryCorp defaultDeliveryCorp) {
		this.defaultDeliveryCorp = defaultDeliveryCorp;
	}

	@ManyToMany(mappedBy = "shippingMethods", fetch = FetchType.LAZY)
	public Set<PaymentMethod> getPaymentMethods() {
		return this.paymentMethods;
	}

	public void setPaymentMethods(Set<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	@OneToMany(mappedBy = "shippingMethod", fetch = FetchType.LAZY)
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@Transient
	public BigDecimal calculateFreight(Integer weight) {
		Setting setting = SettingUtils.get();
		BigDecimal freight = new BigDecimal(0);
		if (weight != null)
			if ((weight.intValue() <= getFirstWeight().intValue())
					|| (getContinuePrice().compareTo(new BigDecimal(0)) == 0)) {
				freight = getFirstPrice();
			} else {
				double d = Math.ceil((weight.intValue() - getFirstWeight()
						.intValue())
						/ getContinueWeight().intValue());
				freight = getFirstPrice().add(
						getContinuePrice().multiply(new BigDecimal(d)));
			}

		return setting.setScale(freight);
	}

	@PreRemove
	public void preRemove() {
		Set<PaymentMethod> setp = getPaymentMethods();
		if (setp != null) {
			Iterator<PaymentMethod> iterator = setp.iterator();
			while (iterator.hasNext()) {
				PaymentMethod paymentMethod = iterator.next();
				paymentMethod.getShippingMethods().remove(this);
			}
		}

		Set<Order> seto = getOrders();
		if (seto != null) {
			Iterator<Order> iterator = seto.iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				order.setShippingMethod(null);
			}
		}
	}
}