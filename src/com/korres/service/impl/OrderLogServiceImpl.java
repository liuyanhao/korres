package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.OrderLogDao;
import com.korres.entity.OrderLog;
import com.korres.service.OrderLogService;
import org.springframework.stereotype.Service;

@Service("orderLogServiceImpl")
public class OrderLogServiceImpl extends BaseServiceImpl<OrderLog, Long>
		implements OrderLogService {
	@Resource(name = "orderLogDaoImpl")
	public void setBaseDao(OrderLogDao orderLogDao) {
		super.setBaseDao(orderLogDao);
	}
}