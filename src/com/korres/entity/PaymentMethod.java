package com.korres.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：PaymentMethod.java
 * 功能说明：支付方式实体类
 * 创建日期：2018-08-28 下午03:17:14
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_payment_method")
public class PaymentMethod extends OrderEntity {
	private static final long serialVersionUID = 6949816500116581199L;
	private String name;
	private PaymentMethodType type;
	private Integer timeout;
	private String icon;
	private String description;
	private String content;
	private Set<ShippingMethod> shippingMethods = new HashSet<ShippingMethod>();
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
	@Column(nullable = false)
	public PaymentMethodType getType() {
		return this.type;
	}

	public void setType(PaymentMethodType type) {
		this.type = type;
	}

	@Min(1L)
	public Integer getTimeout() {
		return this.timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	@Length(max = 200)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Length(max = 200)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Lob
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_payment_shipping_method")
	@OrderBy("order asc")
	public Set<ShippingMethod> getShippingMethods() {
		return this.shippingMethods;
	}

	public void setShippingMethods(Set<ShippingMethod> shippingMethods) {
		this.shippingMethods = shippingMethods;
	}

	@OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@PreRemove
	public void preRemove() {
		Set<Order> set = getOrders();
		if (set != null) {
			Iterator<Order> iterator = set.iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				order.setPaymentMethod(null);
			}
		}
	}

	public enum PaymentMethodType {
		online, offline;
	}

}