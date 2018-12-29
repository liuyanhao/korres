package com.korres.service.impl;

import javax.annotation.Resource;
import com.korres.dao.PaymentMethodDao;
import com.korres.entity.PaymentMethod;
import com.korres.service.PaymentMethodService;
import org.springframework.stereotype.Service;

@Service("paymentMethodServiceImpl")
public class PaymentMethodServiceImpl extends
		BaseServiceImpl<PaymentMethod, Long> implements PaymentMethodService {
	@Resource(name = "paymentMethodDaoImpl")
	public void setBaseDao(PaymentMethodDao paymentMethodDao) {
		super.setBaseDao(paymentMethodDao);
	}
}