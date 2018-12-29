package com.korres.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Returns.java
 * 功能说明：
 * 创建日期：2013-8-28 下午03:31:06
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_returns")
public class Returns extends BaseEntity {
	private static final long serialVersionUID = -8019074120457087212L;
	private String sn;
	private String shippingMethod;
	private String deliveryCorp;
	private String trackingNo;
	private BigDecimal freight;
	private String shipper;
	private String area;
	private String address;
	private String zipCode;
	private String phone;
	private String operator;
	private String memo;
	private Order order;
	private List<ReturnsItem> returnsItems = new ArrayList<ReturnsItem>();

	@Column(nullable = false, updatable = false, unique = true)
	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(updatable = false)
	public String getShippingMethod() {
		return this.shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	@Column(updatable = false)
	public String getDeliveryCorp() {
		return this.deliveryCorp;
	}

	public void setDeliveryCorp(String deliveryCorp) {
		this.deliveryCorp = deliveryCorp;
	}

	@Length(max = 200)
	@Column(updatable = false)
	public String getTrackingNo() {
		return this.trackingNo;
	}

	public void setTrackingNo(String trackingNo) {
		this.trackingNo = trackingNo;
	}

	@Min(0L)
	@Digits(integer = 12, fraction = 3)
	@Column(updatable = false, precision = 21, scale = 6)
	public BigDecimal getFreight() {
		return this.freight;
	}

	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getShipper() {
		return this.shipper;
	}

	public void setShipper(String shipper) {
		this.shipper = shipper;
	}

	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(nullable = false, updatable = false)
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

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders", nullable = false, updatable = false)
	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Valid
	@NotEmpty
	@OneToMany(mappedBy = "returns", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL })
	public List<ReturnsItem> getReturnsItems() {
		return this.returnsItems;
	}

	public void setReturnsItems(List<ReturnsItem> returnsItems) {
		this.returnsItems = returnsItems;
	}

	@Transient
	public int getQuantity() {
		int quantity = 0;
		if (getReturnsItems() != null) {
			Iterator<ReturnsItem> iterator = getReturnsItems().iterator();
			while (iterator.hasNext()) {
				ReturnsItem returnsItem = iterator.next();
				if ((returnsItem != null)
						&& (returnsItem.getQuantity() != null)) {
					quantity += returnsItem.getQuantity().intValue();
				}
			}
		}

		return quantity;
	}
}