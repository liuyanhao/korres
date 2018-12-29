package com.korres.service;

import com.korres.entity.Cart;
import com.korres.entity.Member;

public abstract interface CartService extends BaseService<Cart, Long> {
	public abstract Cart getCurrent();

	public abstract void merge(Member paramMember, Cart paramCart);

	public abstract void evictExpired();
}