package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.DeliveryCenterDao;
import com.korres.entity.DeliveryCenter;
import com.korres.service.DeliveryCenterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("deliveryCenterServiceImpl")
public class DeliveryCenterServiceImpl extends
		BaseServiceImpl<DeliveryCenter, Long> implements DeliveryCenterService {

	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	@Resource(name = "deliveryCenterDaoImpl")
	public void setBaseDao(DeliveryCenterDao DeliveryCenterDao) {
		super.setBaseDao(DeliveryCenterDao);
	}

	@Transactional(readOnly = true)
	public DeliveryCenter findDefault() {
		return this.deliveryCenterDao.findDefault();
	}
}