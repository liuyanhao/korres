package com.korres.dao.impl;

import com.korres.dao.CartItemDao;
import com.korres.entity.CartItem;
import org.springframework.stereotype.Repository;

@Repository("cartItemDaoImpl")
public class CartItemDaoImpl extends BaseDaoImpl<CartItem, Long> implements
		CartItemDao {
}