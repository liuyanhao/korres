package com.korres.dao.impl;

import com.korres.dao.OrderItemDao;
import com.korres.entity.OrderItem;
import org.springframework.stereotype.Repository;

@Repository("orderItemDaoImpl")
public class OrderItemDaoImpl extends BaseDaoImpl<OrderItem, Long> implements
		OrderItemDao {
}