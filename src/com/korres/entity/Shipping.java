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

@Entity
@Table(name = "xx_shipping")
public class Shipping extends BaseEntity {
	private static final long serialVersionUID = -261737051893669935L;
	private String sn;
	private String shippingMethod;
	private String deliveryCorp;
	private String deliveryCorpUrl;
	private String deliveryCorpCode;
	private String trackingNo;
	private BigDecimal freight;
	private String consignee;
	private String area;
	private String address;
	private String zipCode;
	private String phone;
	private String operator;
	private String memo;
	private Order order;
	private List<ShippingItem> shippingItems = new ArrayList<ShippingItem>();

	@Column(nullable = false, updatable = false, unique = true)
	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getShippingMethod() {
		return this.shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getDeliveryCorp() {
		return this.deliveryCorp;
	}

	public void setDeliveryCorp(String deliveryCorp) {
		this.deliveryCorp = deliveryCorp;
	}

	public String getDeliveryCorpUrl() {
		return this.deliveryCorpUrl;
	}

	@Column(updatable = false)
	public void setDeliveryCorpUrl(String deliveryCorpUrl) {
		this.deliveryCorpUrl = deliveryCorpUrl;
	}

	public String getDeliveryCorpCode() {
		return this.deliveryCorpCode;
	}

	@Column(updatable = false)
	public void setDeliveryCorpCode(String deliveryCorpCode) {
		this.deliveryCorpCode = deliveryCorpCode;
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
	public String getConsignee() {
		return this.consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
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
	@OneToMany(mappedBy = "shipping", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL })
	public List<ShippingItem> getShippingItems() {
		return this.shippingItems;
	}

	public void setShippingItems(List<ShippingItem> shippingItems) {
		this.shippingItems = shippingItems;
	}

	@Transient
	public int getQuantity() {
		int quantity = 0;
		if (getShippingItems() != null) {
			Iterator<ShippingItem> iterator = getShippingItems().iterator();
			while (iterator.hasNext()) {
				ShippingItem shippingItem = iterator.next();
				if ((shippingItem != null)
						&& (shippingItem.getQuantity() != null)) {
					quantity += shippingItem.getQuantity().intValue();
				}
			}
		}

		return quantity;
	}
}