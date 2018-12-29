package com.korres.dao;

import com.korres.entity.Cart;

public abstract interface CartDao extends BaseDao<Cart, Long> {
	public abstract void evictExpired();
}