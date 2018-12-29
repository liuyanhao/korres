package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.OrderItemDao;
import com.korres.entity.OrderItem;
import com.korres.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service("orderItemServiceImpl")
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem, Long>
		implements OrderItemService {
	@Resource(name = "orderItemDaoImpl")
	public void setBaseDao(OrderItemDao orderItemDao) {
		super.setBaseDao(orderItemDao);
	}
}