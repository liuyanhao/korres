package com.korres.service.impl;

import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.dao.CartDao;
import com.korres.dao.CartItemDao;
import com.korres.dao.MemberDao;
import com.korres.entity.Cart;
import com.korres.entity.CartItem;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.service.CartService;
import com.korres.util.CookieUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.korres.Principal;

@Service("cartServiceImpl")
public class CartServiceImpl extends BaseServiceImpl<Cart, Long> implements
		CartService {

	@Resource(name = "cartDaoImpl")
	private CartDao cartDao;

	@Resource(name = "cartItemDaoImpl")
	private CartItemDao cartItemDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "cartDaoImpl")
	public void setBaseDao(CartDao cartDao) {
		super.setBaseDao(cartDao);
	}

	public Cart getCurrent() {
		RequestAttributes localRequestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (localRequestAttributes != null) {
			HttpServletRequest localHttpServletRequest = ((ServletRequestAttributes) localRequestAttributes)
					.getRequest();
			Principal localPrincipal = (Principal) localHttpServletRequest
					.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			Member member = localPrincipal != null ? (Member) this.memberDao
					.find(localPrincipal.getId()) : null;
			if (member != null) {
				Cart cart = member.getCart();
				if (cart != null) {
					if (!cart.hasExpired()) {
						if (!DateUtils.isSameDay(cart.getModifyDate(),
								new Date())) {
							cart.setModifyDate(new Date());
							this.cartDao.merge(cart);
						}
						return cart;
					}

					this.cartDao.remove(cart);
				}
			} else {
				String cookie = CookieUtils.getCookie(localHttpServletRequest,
						"cartId");
				String str = CookieUtils.getCookie(localHttpServletRequest,
						"cartKey");
				if (StringUtils.isNotEmpty(cookie)
						&& StringUtils.isNumeric(cookie)
						&& StringUtils.isNotEmpty(str)) {
					Cart localCart = (Cart) this.cartDao.find(Long
							.valueOf(cookie));
					if ((localCart != null) && (localCart.getMember() == null)
							&& (StringUtils.equals(localCart.getKey(), str))) {
						if (!localCart.hasExpired()) {
							if (!DateUtils.isSameDay(localCart.getModifyDate(),
									new Date())) {
								localCart.setModifyDate(new Date());
								this.cartDao.merge(localCart);
							}
							return localCart;
						}
						this.cartDao.remove(localCart);
					}
				}
			}
		}
		return null;
	}

	public void merge(Member member, Cart cart) {
		if ((member != null) && (cart != null) && (cart.getMember() == null)) {
			Cart localCart = member.getCart();
			if (localCart != null) {
				Iterator localIterator = cart.getCartItems().iterator();
				while (localIterator.hasNext()) {
					CartItem localCartItem1 = (CartItem) localIterator.next();
					Product localProduct = localCartItem1.getProduct();
					if (localCart.contains(localProduct)) {
						if ((Cart.MAX_PRODUCT_COUNT == null)
								|| (localCart.getCartItems().size() <= Cart.MAX_PRODUCT_COUNT
										.intValue())) {
							CartItem localCartItem2 = localCart
									.getCartItem(localProduct);
							localCartItem2.add(localCartItem1.getQuantity()
									.intValue());
							this.cartItemDao.merge(localCartItem2);
						}
					} else if ((Cart.MAX_PRODUCT_COUNT == null)
							|| (localCart.getCartItems().size() < Cart.MAX_PRODUCT_COUNT
									.intValue())) {
						localIterator.remove();
						localCartItem1.setCart(localCart);
						localCart.getCartItems().add(localCartItem1);
						this.cartItemDao.merge(localCartItem1);
					}
				}
				this.cartDao.remove(cart);
			} else {
				member.setCart(cart);
				cart.setMember(member);
				this.cartDao.merge(cart);
			}
		}
	}

	public void evictExpired() {
		this.cartDao.evictExpired();
	}
}