package com.korres.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;

/*
 * 类名：Deposit.java
 * 功能说明：帐号存款实体类
 * 创建日期：2018-08-28 下午02:58:14
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_deposit")
public class Deposit extends BaseEntity {
	private static final long serialVersionUID = -8323452873046981882L;
	private DepositType type;
	private BigDecimal credit;
	private BigDecimal debit;
	private BigDecimal balance;
	private String operator;
	private String memo;
	private Member member;
	private Order order;
	private Payment payment;

	@Column(nullable = false, updatable = false)
	public DepositType getType() {
		return this.type;
	}

	public void setType(DepositType type) {
		this.type = type;
	}

	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getCredit() {
		return this.credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getDebit() {
		return this.debit;
	}

	public void setDebit(BigDecimal debit) {
		this.debit = debit;
	}

	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Column(updatable = false)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Length(max = 200)
	@Column(updatable = false)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders")
	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Payment getPayment() {
		return this.payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public enum DepositType {
		memberRecharge, memberPayment, adminRecharge, adminChargeback, adminPayment, adminRefunds;
	}
}