package com.korres.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Member;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Order.OrderShippingStatus;

public abstract interface OrderDao extends
		BaseDao<com.korres.entity.Order, Long> {
	public abstract com.korres.entity.Order findBySn(String paramString);

	public abstract List<com.korres.entity.Order> findList(Member paramMember,
			Integer paramInteger, List<Filter> paramList,
			List<com.korres.Order> paramList1);

	public abstract Page<com.korres.entity.Order> findPage(Member paramMember,
			Pageable paramPageable);

	public abstract Page<com.korres.entity.Order> findPage(
			OrderOrderStatus paramOrderStatus,
			OrderPaymentStatus paramPaymentStatus,
			OrderShippingStatus paramShippingStatus, Boolean paramBoolean,
			Pageable paramPageable);

	public abstract Long count(OrderOrderStatus paramOrderStatus,
			OrderPaymentStatus paramPaymentStatus,
			OrderShippingStatus paramShippingStatus, Boolean paramBoolean);

	public abstract Long waitingPaymentCount(Member paramMember);

	public abstract Long waitingShippingCount(Member paramMember);

	public abstract BigDecimal getSalesAmount(Date paramDate1, Date paramDate2);

	public abstract Integer getSalesVolume(Date paramDate1, Date paramDate2);

	public abstract void releaseStock();
}