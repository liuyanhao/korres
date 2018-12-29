package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.CartItemDao;
import com.korres.entity.CartItem;
import com.korres.service.CartItemService;
import org.springframework.stereotype.Service;

@Service("cartItemServiceImpl")
public class CartItemServiceImpl extends BaseServiceImpl<CartItem, Long>
		implements CartItemService {
	@Resource(name = "cartItemDaoImpl")
	public void setBaseDao(CartItemDao cartItemDao) {
		super.setBaseDao(cartItemDao);
	}
}