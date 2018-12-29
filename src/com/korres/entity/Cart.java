package com.korres.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.korres.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.time.DateUtils;

import com.korres.Setting;

/*
 * 类名：Cart.java
 * 功能说明：购物车实体类
 * 创建日期：2018-08-20 下午04:43:19
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_cart")
public class Cart extends BaseEntity {
	private static final long serialVersionUID = -4041846268150609542L;
	public static final int TIMEOUT = 604800;
	public static final Integer MAX_PRODUCT_COUNT = Integer.valueOf(100);
	public static final String ID_COOKIE_NAME = "cartId";
	public static final String KEY_COOKIE_NAME = "cartKey";
	private String key;
	private Member member;
	private Set<CartItem> cartItems = new HashSet<CartItem>();

	@Column(name = "cart_key", nullable = false, updatable = false)
	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	public Set<CartItem> getCartItems() {
		return this.cartItems;
	}

	public void setCartItems(Set<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	@Transient
	public int getPoint() {
		int i = 0;
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if (cartItem != null)
					i = (int) (i + cartItem.getPoint());
			}
		}

		Iterator<Promotion> localIterator = getPromotions().iterator();
		while (localIterator.hasNext()) {
			Promotion promotion = localIterator.next();
			i = promotion.calculatePoint(Integer.valueOf(i)).intValue();
		}
		return i;
	}

	@Transient
	public int getWeight() {
		int weight = 0;
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if (cartItem != null) {
					weight += cartItem.getWeight();
				}
			}
		}

		return weight;
	}

	@Transient
	public int getQuantity() {
		int quantity = 0;
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if ((cartItem != null) && (cartItem.getQuantity() != null))
					quantity += cartItem.getQuantity().intValue();
			}
		}

		return quantity;
	}

	@Transient
	public BigDecimal getPrice() {
		BigDecimal price = new BigDecimal(0);
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if ((cartItem != null) && (cartItem.getSubtotal() != null)) {
					price = price.add(cartItem.getSubtotal());
				}
			}
		}

		return price;
	}

	@Transient
	public BigDecimal getAmount() {
		Setting setting = SettingUtils.get();
		BigDecimal amount = getPrice();
		Iterator<Promotion> iterator = getPromotions().iterator();
		while (iterator.hasNext()) {
			Promotion promotion = iterator.next();
			amount = promotion.calculatePrice(amount);
		}

		return setting.setScale(amount);
	}

	@Transient
	public BigDecimal getDiscount() {
		BigDecimal discount = getPrice().subtract(getAmount());
		return discount.compareTo(new BigDecimal(0)) > 0 ? discount
				: new BigDecimal(0);
	}

	@Transient
	public Set<GiftItem> getGiftItems() {
		Set<GiftItem> set = new HashSet<GiftItem>();
		Iterator<Promotion> ipt = getPromotions().iterator();
		while (ipt.hasNext()) {
			Promotion promotion = ipt.next();
			if (promotion.getGiftItems() != null) {
				Iterator<GiftItem> igi = promotion.getGiftItems().iterator();
				while (igi.hasNext()) {
					GiftItem gi1 = igi.next();
					GiftItem gi2 = (GiftItem) CollectionUtils.find(set,
							new CartPredicate(this, gi1));
					if (gi2 != null) {
						gi2.setQuantity(Integer.valueOf(gi2.getQuantity()
								.intValue()
								+ gi1.getQuantity().intValue()));
					} else {
						set.add(gi1);
					}
				}
			}
		}

		return set;
	}

	@Transient
	public Set<Promotion> getPromotions() {
		Set<Promotion> set = new HashSet<Promotion>();
		if (getCartItems() != null) {
			Iterator<CartItem> ici = getCartItems().iterator();
			while (ici.hasNext()) {
				CartItem cartItem = ici.next();
				if ((cartItem != null) && (cartItem.getProduct() != null))
					set.addAll(cartItem.getProduct().getValidPromotions());
			}
		}

		Set<Promotion> sp = new TreeSet<Promotion>();
		Iterator<Promotion> iterator = set.iterator();
		while (iterator.hasNext()) {
			Promotion promotion = iterator.next();
			if (key(promotion))
				sp.add(promotion);
		}

		return sp;
	}

	@Transient
	private boolean key(Promotion paramPromotion) {
		if ((paramPromotion == null) || (!paramPromotion.hasBegun())
				|| (paramPromotion.hasEnded())) {
			return false;
		}

		if ((paramPromotion.getMemberRanks() == null)
				|| (getMember() == null)
				|| (getMember().getMemberRank() == null)
				|| (!paramPromotion.getMemberRanks().contains(
						getMember().getMemberRank()))) {
			return false;
		}

		BigDecimal amount = new BigDecimal(0);
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if (cartItem != null) {
					Product product = cartItem.getProduct();
					if (product != null) {
						if ((product.getPromotions() != null)
								&& (product.getPromotions()
										.contains(paramPromotion))) {
							amount = amount.add(cartItem.getSubtotal());
						} else if ((product.getProductCategory() != null)
								&& (product.getProductCategory()
										.getPromotions()
										.contains(paramPromotion))) {
							amount = amount.add(cartItem.getSubtotal());
						} else if ((product.getBrand() != null)
								&& (product.getBrand().getPromotions()
										.contains(paramPromotion))) {
							amount = amount.add(cartItem.getSubtotal());
						}
					}
				}
			}
		}

		return ((paramPromotion.getStartPrice() == null) || (paramPromotion
				.getStartPrice().compareTo(amount) <= 0))
				&& ((paramPromotion.getEndPrice() == null) || (paramPromotion
						.getEndPrice().compareTo(amount) >= 0));
	}

	@Transient
	public boolean isValid(Coupon coupon) {
		if ((coupon == null) || (!coupon.getIsEnabled().booleanValue())
				|| (!coupon.hasBegun()) || (coupon.hasExpired()))
			return false;
		return ((coupon.getStartPrice() == null) || (coupon.getStartPrice()
				.compareTo(getAmount()) <= 0))
				&& ((coupon.getEndPrice() == null) || (coupon.getEndPrice()
						.compareTo(getAmount()) >= 0));
	}

	@Transient
	public CartItem getCartItem(Product product) {
		if ((product != null) && (getCartItems() != null)) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if ((cartItem != null) && (cartItem.getProduct() == product)) {
					return cartItem;
				}
			}
		}

		return null;
	}

	@Transient
	public boolean contains(Product product) {
		if ((product != null) && (getCartItems() != null)) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if ((cartItem != null) && (cartItem.getProduct() == product)) {
					return true;
				}
			}
		}

		return false;
	}

	@Transient
	public String getToken() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37)
				.append(getKey());
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				hashCodeBuilder.append(cartItem.getProduct()).append(
						cartItem.getQuantity()).append(cartItem.getUnitPrice());
			}
		}

		return DigestUtils.md5Hex(hashCodeBuilder.toString());
	}

	@Transient
	public boolean getIsLowStock() {
		if (getCartItems() != null) {
			Iterator<CartItem> iterator = getCartItems().iterator();
			while (iterator.hasNext()) {
				CartItem cartItem = iterator.next();
				if ((cartItem != null) && (cartItem.getIsLowStock())) {
					return true;
				}
			}
		}

		return false;
	}

	@Transient
	public boolean hasExpired() {
		return new Date().after(DateUtils.addSeconds(getModifyDate(), TIMEOUT));
	}

	@Transient
	public boolean isCouponAllowed() {
		Iterator<Promotion> iterator = getPromotions().iterator();
		while (iterator.hasNext()) {
			Promotion promotion = iterator.next();
			if ((promotion != null)
					&& (!promotion.getIsCouponAllowed().booleanValue())) {
				return false;
			}
		}

		return true;
	}

	@Transient
	public boolean isEmpty() {
		return (getCartItems() == null) || (getCartItems().isEmpty());
	}

	public class CartPredicate implements Predicate {
		private Cart paramCart;
		private GiftItem paramGiftItem;

		CartPredicate(Cart paramCart, GiftItem paramGiftItem) {
			this.paramCart = paramCart;
			this.paramGiftItem = paramGiftItem;
		}

		public boolean evaluate(Object object) {
			GiftItem giftItem = (GiftItem) object;
			return (giftItem != null)
					&& (giftItem.getGift().equals(this.paramGiftItem.getGift()));
		}
	}
}