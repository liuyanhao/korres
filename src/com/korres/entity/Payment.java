package com.korres.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/*
 * 类名：Payment.java
 * 功能说明：支付实体类
 * 创建日期：2018-08-28 下午03:16:30
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_payment")
public class Payment extends BaseEntity {
	private static final long serialVersionUID = -5052430116564638634L;
	public static final String TYPE_SEPARATOR = "-";
	private String sn;
	private PaymentType type;
	private PaymentStatus status;
	private String paymentMethod;
	private String bank;
	private String account;
	private BigDecimal fee;
	private BigDecimal amount;
	private String payer;
	private String operator;
	private Date paymentDate;
	private String memo;
	private String paymentPluginId;
	private Date expire;
	private Deposit deposit;
	private Member member;
	private Order order;

	@Column(nullable = false, updatable = false, unique = true)
	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@NotNull
	@Column(nullable = false, updatable = false)
	public PaymentType getType() {
		return this.type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	@Column(nullable = false)
	public PaymentStatus getStatus() {
		return this.status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	@Column(updatable = false)
	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Length(max = 200)
	public String getBank() {
		return this.bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	@Length(max = 200)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getFee() {
		return this.fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@NotNull
	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Length(max = 200)
	public String getPayer() {
		return this.payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	@Column(updatable = false)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Length(max = 200)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@JoinColumn(updatable = false)
	public String getPaymentPluginId() {
		return this.paymentPluginId;
	}

	public void setPaymentPluginId(String paymentPluginId) {
		this.paymentPluginId = paymentPluginId;
	}

	@JoinColumn(updatable = false)
	public Date getExpire() {
		return this.expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	@OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
	public Deposit getDeposit() {
		return this.deposit;
	}

	public void setDeposit(Deposit deposit) {
		this.deposit = deposit;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders", updatable = false)
	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Transient
	public boolean hasExpired() {
		return (getExpire() != null) && (new Date().after(getExpire()));
	}

	@PreRemove
	public void preRemove() {
		if (getDeposit() != null) {
			getDeposit().setPayment(null);
		}
	}

	public enum PaymentStatus {
		wait, success, failure;
	}

	public enum PaymentType {
		online, offline, deposit;
	}
}