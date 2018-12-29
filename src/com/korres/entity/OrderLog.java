package com.korres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/*
 * 类名：OrderLog.java
 * 功能说明：订单日志实体类
 * 创建日期：2018-12-28 下午03:15:08
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_order_log")
public class OrderLog extends BaseEntity {
	private static final long serialVersionUID = -2704154761295319939L;
	private OrderLogType type;
	private String operator;
	private String content;
	private Order order;

	public OrderLog() {
	}

	public OrderLog(OrderLogType type, String operator) {
		this.type = type;
		this.operator = operator;
	}

	public OrderLog(OrderLogType type, String operator, String content) {
		this.type = type;
		this.operator = operator;
		this.content = content;
	}

	@Column(nullable = false, updatable = false)
	public OrderLogType getType() {
		return this.type;
	}

	public void setType(OrderLogType type) {
		this.type = type;
	}

	@Column(updatable = false)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(updatable = false)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders", nullable = false, updatable = false)
	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public enum OrderLogType {
		create, modify, confirm, payment, refunds, shipping, returns, complete, cancel, other;
	}
}