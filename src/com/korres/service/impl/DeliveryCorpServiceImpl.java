package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.DeliveryCorpDao;
import com.korres.entity.DeliveryCorp;
import com.korres.service.DeliveryCorpService;
import org.springframework.stereotype.Service;

@Service("deliveryCorpServiceImpl")
public class DeliveryCorpServiceImpl extends
		BaseServiceImpl<DeliveryCorp, Long> implements DeliveryCorpService {
	@Resource(name = "deliveryCorpDaoImpl")
	public void setBaseDao(DeliveryCorpDao deliveryCorpDao) {
		super.setBaseDao(deliveryCorpDao);
	}
}