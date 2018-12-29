package com.korres;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * 类名：Order.java
 * 功能说明：排序条件类
 * 创建日期：2018-12-9 下午01:54:18
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class Order implements Serializable {
	private static final OrderDirection ORDER_DIRECTION = OrderDirection.desc;
	private String property;
	private OrderDirection direction = ORDER_DIRECTION;

	public Order() {
	}

	public Order(String property, OrderDirection direction) {
		this.property = property;
		this.direction = direction;
	}

	public static Order asc(String property) {
		return new Order(property, OrderDirection.asc);
	}

	public static Order desc(String property) {
		return new Order(property, OrderDirection.desc);
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public OrderDirection getDirection() {
		return this.direction;
	}

	public void setDirection(OrderDirection direction) {
		this.direction = direction;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this == obj)
			return true;
		Order localOrder = (Order) obj;
		return new EqualsBuilder()
				.append(getProperty(), localOrder.getProperty())
				.append(getDirection(), localOrder.getDirection()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getProperty())
				.append(getDirection()).toHashCode();
	}

	public enum OrderDirection {
		asc, desc;
		public static OrderDirection fromString(String value) {
			return valueOf(value.toLowerCase());
		}
	}
}