package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.ShippingMethodDao;
import com.korres.entity.ShippingMethod;
import com.korres.service.ShippingMethodService;
import org.springframework.stereotype.Service;

@Service("shippingMethodServiceImpl")
public class ShippingMethodServiceImpl extends
		BaseServiceImpl<ShippingMethod, Long> implements ShippingMethodService {
	@Resource(name = "shippingMethodDaoImpl")
	public void setBaseDao(ShippingMethodDao shippingMethodDao) {
		super.setBaseDao(shippingMethodDao);
	}
}