package com.korres.dao;

import com.korres.entity.Payment;

public abstract interface PaymentDao extends BaseDao<Payment, Long> {
	public abstract Payment findBySn(String paramString);
}