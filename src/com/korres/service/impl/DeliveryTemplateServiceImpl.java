package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.DeliveryTemplateDao;
import com.korres.entity.DeliveryTemplate;
import com.korres.service.DeliveryTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("deliveryTemplateServiceImpl")
public class DeliveryTemplateServiceImpl extends
		BaseServiceImpl<DeliveryTemplate, Long> implements
		DeliveryTemplateService {

	@Resource(name = "deliveryTemplateDaoImpl")
	private DeliveryTemplateDao deliveryTemplateDao;

	@Resource(name = "deliveryTemplateDaoImpl")
	public void setBaseDao(DeliveryTemplateDao deliveryTemplateDao) {
		super.setBaseDao(deliveryTemplateDao);
	}

	@Transactional(readOnly = true)
	public DeliveryTemplate findDefault() {
		return this.deliveryTemplateDao.findDefault();
	}
}