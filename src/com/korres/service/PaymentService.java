package com.korres.service;

import com.korres.entity.Payment;

public abstract interface PaymentService extends BaseService<Payment, Long> {
	public abstract Payment findBySn(String paramString);
}