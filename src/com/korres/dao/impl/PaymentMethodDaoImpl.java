package com.korres.dao.impl;

import com.korres.dao.PaymentMethodDao;
import com.korres.entity.PaymentMethod;
import org.springframework.stereotype.Repository;

@Repository("paymentMethodDaoImpl")
public class PaymentMethodDaoImpl extends BaseDaoImpl<PaymentMethod, Long>
		implements PaymentMethodDao {
}