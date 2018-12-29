package com.korres.service;

import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.ProductNotify;

public abstract interface ProductNotifyService extends
		BaseService<ProductNotify, Long> {
	public abstract boolean exists(Product paramProduct, String paramString);

	public abstract Page<ProductNotify> findPage(Member paramMember,
			Boolean paramBoolean1, Boolean paramBoolean2,
			Boolean paramBoolean3, Pageable paramPageable);

	public abstract Long count(Member paramMember, Boolean paramBoolean1,
			Boolean paramBoolean2, Boolean paramBoolean3);

	public abstract int send(Long[] paramArrayOfLong);
}