package com.korres.entity;

import java.math.BigDecimal;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.korres.Setting;

import com.korres.util.SettingUtils;

/*
 * 类名：CartItem.java
 * 功能说明：购物车类别实体类
 * 创建日期：2013-8-28 下午02:36:49
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_cart_item")
public class CartItem extends BaseEntity {
	private static final long serialVersionUID = 2979296789363163144L;
	public static final Integer MAX_QUANTITY = Integer.valueOf(10000);
	private Integer quantity;
	private Product product;
	private Cart cart;

	@Column(nullable = false)
	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Cart getCart() {
		return this.cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	@Transient
	public long getPoint() {
		if ((getProduct() != null) && (getProduct().getPoint() != null)
				&& (getQuantity() != null)) {
			return getProduct().getPoint().longValue()
					* getQuantity().intValue();
		}

		return 0L;
	}

	@Transient
	public int getWeight() {
		if ((getProduct() != null) && (getProduct().getWeight() != null)
				&& (getQuantity() != null)) {
			return getProduct().getWeight().intValue()
					* getQuantity().intValue();
		}

		return 0;
	}

	@Transient
	public BigDecimal getUnitPrice() {
		if ((getProduct() != null) && (getProduct().getPrice() != null)) {
			Setting setting = SettingUtils.get();
			if ((getCart() != null) && (getCart().getMember() != null)
					&& (getCart().getMember().getMemberRank() != null)) {
				MemberRank memberRank = getCart().getMember().getMemberRank();
				Map<MemberRank, BigDecimal> map = getProduct().getMemberPrice();
				if ((map != null) && (!map.isEmpty())
						&& (map.containsKey(memberRank))) {
					return setting.setScale(map.get(memberRank));
				}

				if (memberRank.getScale() != null) {
					return setting.setScale(getProduct().getPrice()
							.multiply(
									new BigDecimal(memberRank.getScale()
											.doubleValue())));
				}
			}

			return setting.setScale(getProduct().getPrice());
		}

		return new BigDecimal(0);
	}

	@Transient
	public BigDecimal getSubtotal() {
		if (getQuantity() != null) {
			return getUnitPrice().multiply(
					new BigDecimal(getQuantity().intValue()));
		}

		return new BigDecimal(0);
	}

	@Transient
	public boolean getIsLowStock() {
		return (getQuantity() != null)
				&& (getProduct() != null)
				&& (getProduct().getStock() != null)
				&& (getQuantity().intValue() > getProduct().getAvailableStock()
						.intValue());
	}

	@Transient
	public void add(int quantity) {
		if (quantity > 0) {
			if (getQuantity() != null) {
				setQuantity(Integer
						.valueOf(getQuantity().intValue() + quantity));
			} else {
				setQuantity(Integer.valueOf(quantity));
			}
		}
	}
}