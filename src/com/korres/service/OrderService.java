package com.korres.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;

import com.korres.entity.Admin;
import com.korres.entity.Cart;
import com.korres.entity.CouponCode;
import com.korres.entity.Member;
import com.korres.entity.Payment;
import com.korres.entity.PaymentMethod;
import com.korres.entity.Receiver;
import com.korres.entity.Refunds;
import com.korres.entity.Returns;
import com.korres.entity.Shipping;
import com.korres.entity.ShippingMethod;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Order.OrderShippingStatus;

public abstract interface OrderService extends
		BaseService<com.korres.entity.Order, Long> {
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

	public abstract com.korres.entity.Order build(Cart paramCart,
			Receiver paramReceiver, PaymentMethod paramPaymentMethod,
			ShippingMethod paramShippingMethod, CouponCode paramCouponCode,
			boolean paramBoolean1, String paramString1, boolean paramBoolean2,
			String paramString2);

	public abstract com.korres.entity.Order create(Cart paramCart,
			Receiver paramReceiver, PaymentMethod paramPaymentMethod,
			ShippingMethod paramShippingMethod, CouponCode paramCouponCode,
			boolean paramBoolean1, String paramString1, boolean paramBoolean2,
			String paramString2, Admin paramAdmin);

	public abstract void update(com.korres.entity.Order paramOrder,
			Admin paramAdmin);

	public abstract void confirm(com.korres.entity.Order paramOrder,
			Admin paramAdmin);

	public abstract void complete(com.korres.entity.Order paramOrder,
			Admin paramAdmin);

	public abstract void cancel(com.korres.entity.Order paramOrder,
			Admin paramAdmin);

	public abstract void payment(com.korres.entity.Order paramOrder,
			Payment paramPayment, Admin paramAdmin);

	public abstract void refunds(com.korres.entity.Order paramOrder,
			Refunds paramRefunds, Admin paramAdmin);

	public abstract void shipping(com.korres.entity.Order paramOrder,
			Shipping paramShipping, Admin paramAdmin);

	public abstract void returns(com.korres.entity.Order paramOrder,
			Returns paramReturns, Admin paramAdmin);
}