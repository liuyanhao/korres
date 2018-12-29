package com.korres.dao.impl;

import com.korres.dao.OrderLogDao;
import com.korres.entity.OrderLog;
import org.springframework.stereotype.Repository;

@Repository("orderLogDaoImpl")
public class OrderLogDaoImpl extends BaseDaoImpl<OrderLog, Long> implements
		OrderLogDao {
}