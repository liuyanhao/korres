package com.korres.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import com.korres.dao.CartDao;
import com.korres.dao.CouponCodeDao;
import com.korres.dao.DepositDao;
import com.korres.dao.MemberDao;
import com.korres.dao.MemberRankDao;
import com.korres.dao.OrderDao;
import com.korres.dao.OrderItemDao;
import com.korres.dao.OrderLogDao;
import com.korres.dao.PaymentDao;
import com.korres.dao.ProductDao;
import com.korres.dao.RefundsDao;
import com.korres.dao.ReturnsDao;
import com.korres.dao.ShippingDao;
import com.korres.dao.SnDao;
import com.korres.entity.Admin;
import com.korres.entity.Cart;
import com.korres.entity.CartItem;
import com.korres.entity.Coupon;
import com.korres.entity.CouponCode;
import com.korres.entity.Deposit;
import com.korres.entity.GiftItem;
import com.korres.entity.Member;
import com.korres.entity.MemberRank;
import com.korres.entity.OrderItem;
import com.korres.entity.OrderLog;
import com.korres.entity.Payment;
import com.korres.entity.PaymentMethod;
import com.korres.entity.Product;
import com.korres.entity.Promotion;
import com.korres.entity.Receiver;
import com.korres.entity.Refunds;
import com.korres.entity.Returns;
import com.korres.entity.ReturnsItem;
import com.korres.entity.Shipping;
import com.korres.entity.ShippingItem;
import com.korres.entity.ShippingMethod;
import com.korres.entity.Deposit.DepositType;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Order.OrderShippingStatus;
import com.korres.entity.OrderLog.OrderLogType;
import com.korres.entity.Payment.PaymentType;
import com.korres.entity.PaymentMethod.PaymentMethodType;
import com.korres.entity.Refunds.RefundsType;
import com.korres.entity.Sn.SnType;
import com.korres.service.OrderService;
import com.korres.service.StaticService;
import com.korres.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.korres.Filter;
import com.korres.Page;
import com.korres.Pageable;
import com.korres.Setting;

@Service("orderServiceImpl")
public class OrderServiceImpl extends
		BaseServiceImpl<com.korres.entity.Order, Long> implements OrderService {

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;

	@Resource(name = "orderItemDaoImpl")
	private OrderItemDao orderItemDao;

	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;

	@Resource(name = "cartDaoImpl")
	private CartDao cartDao;

	@Resource(name = "couponCodeDaoImpl")
	private CouponCodeDao couponCodeDao;

	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "memberRankDaoImpl")
	private MemberRankDao memberRankDao;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "paymentDaoImpl")
	private PaymentDao paymentDao;

	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;

	@Resource(name = "shippingDaoImpl")
	private ShippingDao shippingDao;

	@Resource(name = "returnsDaoImpl")
	private ReturnsDao returnsDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "orderDaoImpl")
	public void setBaseDao(OrderDao orderDao) {
		super.setBaseDao(orderDao);
	}

	@Transactional(readOnly = true)
	public com.korres.entity.Order findBySn(String sn) {
		return this.orderDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public List<com.korres.entity.Order> findList(Member member, Integer count,
			List<Filter> filters, List<com.korres.Order> orders) {
		return this.orderDao.findList(member, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<com.korres.entity.Order> findPage(Member member,
			Pageable pageable) {
		return this.orderDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Page<com.korres.entity.Order> findPage(OrderOrderStatus orderStatus,
			OrderPaymentStatus paymentStatus,
			OrderShippingStatus shippingStatus, Boolean hasExpired,
			Pageable pageable) {
		return this.orderDao.findPage(orderStatus, paymentStatus,
				shippingStatus, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(OrderOrderStatus orderStatus,
			OrderPaymentStatus paymentStatus,
			OrderShippingStatus shippingStatus, Boolean hasExpired) {
		return this.orderDao.count(orderStatus, paymentStatus, shippingStatus,
				hasExpired);
	}

	@Transactional(readOnly = true)
	public Long waitingPaymentCount(Member member) {
		return this.orderDao.waitingPaymentCount(member);
	}

	@Transactional(readOnly = true)
	public Long waitingShippingCount(Member member) {
		return this.orderDao.waitingShippingCount(member);
	}

	@Transactional(readOnly = true)
	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		return this.orderDao.getSalesAmount(beginDate, endDate);
	}

	@Transactional(readOnly = true)
	public Integer getSalesVolume(Date beginDate, Date endDate) {
		return this.orderDao.getSalesVolume(beginDate, endDate);
	}

	public void releaseStock() {
		this.orderDao.releaseStock();
	}

	@Transactional(readOnly = true)
	public com.korres.entity.Order build(Cart cart, Receiver receiver,
			PaymentMethod paymentMethod, ShippingMethod shippingMethod,
			CouponCode couponCode, boolean isInvoice, String invoiceTitle,
			boolean useBalance, String memo) {
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.notEmpty(cart.getCartItems());
		com.korres.entity.Order order = new com.korres.entity.Order();
		order.setShippingStatus(OrderShippingStatus.unshipped);
		order.setFee(new BigDecimal(0));
		order.setDiscount(cart.getDiscount());
		order.setPoint(Integer.valueOf(cart.getPoint()));
		order.setMemo(memo);
		order.setMember(cart.getMember());

		if (receiver != null) {
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setArea(receiver.getArea());
		}

		if (!cart.getPromotions().isEmpty()) {
			StringBuffer stringBuffer = new StringBuffer();
			Iterator<Promotion> iterator = cart.getPromotions().iterator();
			while (iterator.hasNext()) {
				Promotion promotion = iterator.next();
				if ((promotion != null) && (promotion.getName() != null)) {
					stringBuffer.append(" " + promotion.getName());
				}
			}

			if (stringBuffer.length() > 0) {
				stringBuffer.deleteCharAt(0);
			}

			order.setPromotion(stringBuffer.toString());
		}

		order.setPaymentMethod(paymentMethod);
		if ((shippingMethod != null)
				&& (paymentMethod != null)
				&& (paymentMethod.getShippingMethods().contains(shippingMethod))) {
			BigDecimal freight = shippingMethod.calculateFreight(Integer
					.valueOf(cart.getWeight()));
			Iterator<Promotion> iterator = cart.getPromotions().iterator();
			while (iterator.hasNext()) {
				Promotion promotion = iterator.next();
				if (promotion.getIsFreeShipping().booleanValue()) {
					freight = new BigDecimal(0);
					break;
				}
			}

			order.setFreight(freight);
			order.setShippingMethod(shippingMethod);
		} else {
			order.setFreight(new BigDecimal(0));
		}

		if ((couponCode != null) && (cart.isCouponAllowed())) {
			this.couponCodeDao.lock(couponCode, LockModeType.PESSIMISTIC_READ);
			if ((!couponCode.getIsUsed().booleanValue())
					&& (couponCode.getCoupon() != null)
					&& (cart.isValid(couponCode.getCoupon()))) {
				BigDecimal amount = couponCode.getCoupon().calculatePrice(
						cart.getAmount());
				BigDecimal discount = cart.getAmount().subtract(amount);
				if (discount.compareTo(new BigDecimal(0)) > 0) {
					order.setDiscount(cart.getDiscount().add(discount));
				}

				order.setCouponCode(couponCode);
			}
		}

		List<OrderItem> lioi = order.getOrderItems();
		Iterator<CartItem> iterator = cart.getCartItems().iterator();
		// Product localProduct;
		// OrderItem localOrderItem;
		while (iterator.hasNext()) {
			CartItem cartItem = iterator.next();
			if ((cartItem != null) && (cartItem.getProduct() != null)) {
				Product product = cartItem.getProduct();
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				orderItem.setPrice(cartItem.getUnitPrice());
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(Boolean.valueOf(false));
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(Integer.valueOf(0));
				orderItem.setReturnQuantity(Integer.valueOf(0));
				orderItem.setProduct(product);
				orderItem.setOrder(order);
				lioi.add(orderItem);
			}
		}

		Iterator<GiftItem> ligi = cart.getGiftItems().iterator();
		while (ligi.hasNext()) {
			GiftItem giftItem = ligi.next();
			if ((giftItem != null) && (giftItem.getGift() != null)) {
				Product product = giftItem.getGift();
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				orderItem.setPrice(new BigDecimal(0));
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(Boolean.valueOf(true));
				orderItem.setQuantity(giftItem.getQuantity());
				orderItem.setShippedQuantity(Integer.valueOf(0));
				orderItem.setReturnQuantity(Integer.valueOf(0));
				orderItem.setProduct(product);
				orderItem.setOrder(order);
				lioi.add(orderItem);
			}
		}

		Setting setting = SettingUtils.get();
		if ((setting.getIsInvoiceEnabled().booleanValue()) && (isInvoice)
				&& (StringUtils.isNotEmpty(invoiceTitle))) {
			order.setIsInvoice(Boolean.valueOf(true));
			order.setInvoiceTitle(invoiceTitle);
			order.setTax(order.calculateTax());
		} else {
			order.setIsInvoice(Boolean.valueOf(false));
			order.setTax(new BigDecimal(0));
		}

		if (useBalance) {
			Member member = cart.getMember();
			if (member.getBalance().compareTo(order.getAmount()) >= 0)
				order.setAmountPaid(order.getAmount());
			else
				order.setAmountPaid(member.getBalance());
		} else {
			order.setAmountPaid(new BigDecimal(0));
		}

		if (order.getAmountPayable().compareTo(new BigDecimal(0)) == 0) {
			order.setOrderStatus(OrderOrderStatus.confirmed);
			order.setPaymentStatus(OrderPaymentStatus.paid);
		} else if ((order.getAmountPayable().compareTo(new BigDecimal(0)) > 0)
				&& (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0)) {
			order.setOrderStatus(OrderOrderStatus.confirmed);
			order.setPaymentStatus(OrderPaymentStatus.partialPayment);
		} else {
			order.setOrderStatus(OrderOrderStatus.unconfirmed);
			order.setPaymentStatus(OrderPaymentStatus.unpaid);
		}

		if ((paymentMethod != null) && (paymentMethod.getTimeout() != null)
				&& (order.getPaymentStatus() == OrderPaymentStatus.unpaid)) {
			order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod
					.getTimeout().intValue()));
		}

		return order;
	}

	public com.korres.entity.Order create(Cart cart, Receiver receiver,
			PaymentMethod paymentMethod, ShippingMethod shippingMethod,
			CouponCode couponCode, boolean isInvoice, String invoiceTitle,
			boolean useBalance, String memo, Admin operator) {
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.notEmpty(cart.getCartItems());
		Assert.notNull(receiver);
		Assert.notNull(paymentMethod);
		Assert.notNull(shippingMethod);
		com.korres.entity.Order order = build(cart, receiver, paymentMethod,
				shippingMethod, couponCode, isInvoice, invoiceTitle,
				useBalance, memo);
		order.setSn(this.snDao.generate(SnType.order));

		if (paymentMethod.getType() == PaymentMethodType.online) {
			order.setLockExpire(DateUtils.addSeconds(new Date(), 10));
			order.setOperator(operator);
		}

		if (order.getCouponCode() != null) {
			couponCode.setIsUsed(Boolean.valueOf(true));
			couponCode.setUsedDate(new Date());
			this.couponCodeDao.merge(couponCode);
		}

		Iterator<Promotion> iterator = cart.getPromotions().iterator();
		// Object localObject4;
		while (iterator.hasNext()) {
			Promotion promotion = (Promotion) iterator.next();
			Iterator iterator2 = promotion.getCoupons().iterator();
			while (iterator2.hasNext()) {
				Coupon coupon = (Coupon) iterator2.next();
				order.getCoupons().add(coupon);
			}
		}

		Setting setting = SettingUtils.get();
		if ((setting.getStockAllocationTime() == Setting.StockAllocationTime.order)
				|| ((setting.getStockAllocationTime() == Setting.StockAllocationTime.payment) && ((order
						.getPaymentStatus() == OrderPaymentStatus.partialPayment) || (order
						.getPaymentStatus() == OrderPaymentStatus.paid)))) {
			order.setIsAllocatedStock(Boolean.valueOf(true));
		} else {
			order.setIsAllocatedStock(Boolean.valueOf(false));
		}

		this.orderDao.persist(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.create);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		this.orderLogDao.persist(orderLog);
		Member member = cart.getMember();
		if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			member.setBalance(member.getBalance().subtract(
					order.getAmountPaid()));
			this.memberDao.merge(member);
			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? DepositType.adminPayment
					: DepositType.memberPayment);
			deposit.setCredit(new BigDecimal(0));
			deposit.setDebit(order.getAmountPaid());
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername()
					: null);
			deposit.setMember(member);
			deposit.setOrder(order);
			this.depositDao.persist(deposit);
		}

		if ((setting.getStockAllocationTime() == Setting.StockAllocationTime.order)
				|| ((setting.getStockAllocationTime() == Setting.StockAllocationTime.payment) && ((order
						.getPaymentStatus() == OrderPaymentStatus.partialPayment) || (order
						.getPaymentStatus() == OrderPaymentStatus.paid)))) {
			Iterator<OrderItem> io = order.getOrderItems().iterator();
			while (io.hasNext()) {
				OrderItem orderItem = io.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												+ (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}
		}

		this.cartDao.remove(cart);

		return order;
	}

	public void update(com.korres.entity.Order order, Admin operator) {
		Assert.notNull(order);
		com.korres.entity.Order o = (com.korres.entity.Order) this.orderDao
				.find(order.getId());
		if (o.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> iterator = o.getOrderItems().iterator();
			while (iterator.hasNext()) {
				OrderItem orderItem = iterator.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												- (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}

			Iterator<OrderItem> iterator2 = order.getOrderItems().iterator();
			while (iterator2.hasNext()) {
				OrderItem orderItem = iterator2.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												+ (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.productDao.flush();
						this.staticService.build(product);
					}
				}
			}
		}

		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.modify);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		this.orderLogDao.persist(orderLog);
	}

	public void confirm(com.korres.entity.Order order, Admin operator) {
		Assert.notNull(order);
		order.setOrderStatus(OrderOrderStatus.confirmed);
		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.confirm);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		this.orderLogDao.persist(orderLog);
	}

	public void complete(com.korres.entity.Order order, Admin operator) {
		Assert.notNull(order);
		Member member = order.getMember();
		this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
		if ((order.getShippingStatus() == OrderShippingStatus.partialShipment)
				|| (order.getShippingStatus() == OrderShippingStatus.shipped)) {
			member.setPoint(Long.valueOf(member.getPoint().longValue()
					+ order.getPoint().intValue()));
			Iterator<Coupon> iterator = order.getCoupons().iterator();
			while (iterator.hasNext()) {
				Coupon coupon = iterator.next();
				this.couponCodeDao.build(coupon, member);
			}
		}

		if ((order.getShippingStatus() == OrderShippingStatus.unshipped)
				|| (order.getShippingStatus() == OrderShippingStatus.returned)) {
			CouponCode couponCode = order.getCouponCode();
			if (couponCode != null) {
				couponCode.setIsUsed(Boolean.valueOf(false));
				couponCode.setUsedDate(null);
				this.couponCodeDao.merge(couponCode);
				order.setCouponCode(null);
				this.orderDao.merge(order);
			}
		}

		member.setAmount(member.getAmount().add(order.getAmountPaid()));
		if (!member.getMemberRank().getIsSpecial().booleanValue()) {
			MemberRank memberRank = this.memberRankDao.findByAmount(member
					.getAmount());
			if ((memberRank != null)
					&& (memberRank.getAmount().compareTo(
							member.getMemberRank().getAmount()) > 0)) {
				member.setMemberRank(memberRank);
			}
		}

		this.memberDao.merge(member);
		if (order.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> iterator = order.getOrderItems().iterator();
			while (iterator.hasNext()) {
				OrderItem orderItem = iterator.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												- (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}
			order.setIsAllocatedStock(Boolean.valueOf(false));
		}

		Iterator<OrderItem> localIterator = order.getOrderItems().iterator();
		while (localIterator.hasNext()) {
			OrderItem orderItem = localIterator.next();
			if (orderItem != null) {
				Product product = orderItem.getProduct();
				this.productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				if (product != null) {
					Integer quantity = orderItem.getQuantity();
					Calendar calendar = Calendar.getInstance();
					Calendar weekSalesDate = DateUtils.toCalendar(product
							.getWeekSalesDate());
					Calendar monthSalesDate = DateUtils.toCalendar(product
							.getMonthSalesDate());
					if ((calendar.get(1) != weekSalesDate.get(1))
							|| (calendar.get(3) > weekSalesDate.get(3))) {
						product.setWeekSales(Long.valueOf(quantity.intValue()));
					} else {
						product.setWeekSales(Long.valueOf(product
								.getWeekSales().longValue()
								+ quantity.intValue()));
					}

					if ((calendar.get(1) != monthSalesDate.get(1))
							|| (calendar.get(2) > monthSalesDate.get(2))) {
						product
								.setMonthSales(Long
										.valueOf(quantity.intValue()));
					} else {
						product.setMonthSales(Long.valueOf(product
								.getMonthSales().longValue()
								+ quantity.intValue()));
					}

					product.setSales(Long.valueOf(product.getSales()
							.longValue()
							+ quantity.intValue()));
					product.setWeekSalesDate(new Date());
					product.setMonthSalesDate(new Date());
					this.productDao.merge(product);
					this.orderDao.flush();
					this.staticService.build(product);
				}
			}
		}

		order.setOrderStatus(OrderOrderStatus.completed);
		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.complete);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);

		this.orderLogDao.persist(orderLog);
	}

	public void cancel(com.korres.entity.Order order, Admin operator) {
		Assert.notNull(order);
		CouponCode couponCode = order.getCouponCode();
		if (couponCode != null) {
			couponCode.setIsUsed(Boolean.valueOf(false));
			couponCode.setUsedDate(null);
			this.couponCodeDao.merge(couponCode);
			order.setCouponCode(null);
			this.orderDao.merge(order);
		}
		if (order.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> iterator = order.getOrderItems().iterator();
			while (iterator.hasNext()) {
				OrderItem orderItem = iterator.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												- (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}

			order.setIsAllocatedStock(Boolean.valueOf(false));
		}

		order.setOrderStatus(OrderOrderStatus.cancelled);
		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.cancel);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);

		this.orderLogDao.persist(orderLog);
	}

	public void payment(com.korres.entity.Order order, Payment payment,
			Admin operator) {
		Assert.notNull(order);
		Assert.notNull(payment);
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		payment.setOrder(order);
		this.paymentDao.merge(payment);
		if (payment.getType() == PaymentType.deposit) {
			Member member = order.getMember();
			this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			member
					.setBalance(member.getBalance().subtract(
							payment.getAmount()));
			this.memberDao.merge(member);
			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? DepositType.adminPayment
					: DepositType.memberPayment);
			deposit.setCredit(new BigDecimal(0));
			deposit.setDebit(payment.getAmount());
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername()
					: null);
			deposit.setMember(member);
			deposit.setOrder(order);
			this.depositDao.persist(deposit);
		}

		Setting setting = SettingUtils.get();
		if ((!order.getIsAllocatedStock().booleanValue())
				&& (setting.getStockAllocationTime() == Setting.StockAllocationTime.payment)) {
			Iterator<OrderItem> iterator = order.getOrderItems().iterator();
			while (iterator.hasNext()) {
				OrderItem orderItem = iterator.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												+ (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}

			order.setIsAllocatedStock(Boolean.valueOf(true));
		}

		order.setAmountPaid(order.getAmountPaid().add(payment.getAmount()));
		order.setFee(payment.getFee());
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(order.getAmount()) >= 0) {
			order.setOrderStatus(OrderOrderStatus.confirmed);
			order.setPaymentStatus(OrderPaymentStatus.paid);
		} else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setOrderStatus(OrderOrderStatus.confirmed);
			order.setPaymentStatus(OrderPaymentStatus.partialPayment);
		}

		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.payment);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);

		this.orderLogDao.persist(orderLog);
	}

	public void refunds(com.korres.entity.Order order, Refunds refunds,
			Admin operator) {
		Assert.notNull(order);
		Assert.notNull(refunds);
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		refunds.setOrder(order);
		this.refundsDao.persist(refunds);

		if (refunds.getType() == RefundsType.deposit) {
			Member member = order.getMember();
			this.memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			member.setBalance(member.getBalance().add(refunds.getAmount()));
			this.memberDao.merge(member);
			Deposit localDeposit = new Deposit();
			localDeposit.setType(DepositType.adminRefunds);
			localDeposit.setCredit(refunds.getAmount());
			localDeposit.setDebit(new BigDecimal(0));
			localDeposit.setBalance(member.getBalance());
			localDeposit.setOperator(operator != null ? operator.getUsername()
					: null);
			localDeposit.setMember(member);
			localDeposit.setOrder(order);
			this.depositDao.persist(localDeposit);
		}

		order
				.setAmountPaid(order.getAmountPaid().subtract(
						refunds.getAmount()));
		order.setExpire(null);

		if (order.getAmountPaid().compareTo(new BigDecimal(0)) == 0) {
			order.setPaymentStatus(OrderPaymentStatus.refunded);
		} else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setPaymentStatus(OrderPaymentStatus.partialRefunds);
		}

		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.refunds);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);

		this.orderLogDao.persist(orderLog);
	}

	public void shipping(com.korres.entity.Order order, Shipping shipping,
			Admin operator) {
		Assert.notNull(order);
		Assert.notNull(shipping);
		Assert.notEmpty(shipping.getShippingItems());
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);

		Setting setting = SettingUtils.get();
		if ((!order.getIsAllocatedStock().booleanValue())
				&& (setting.getStockAllocationTime() == Setting.StockAllocationTime.ship)) {
			Iterator<OrderItem> iterator = order.getOrderItems().iterator();
			while (iterator.hasNext()) {
				OrderItem orderItem = iterator.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												+ (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}
			order.setIsAllocatedStock(Boolean.valueOf(true));
		}

		shipping.setOrder(order);
		this.shippingDao.persist(shipping);
		Iterator<ShippingItem> iterator = shipping.getShippingItems()
				.iterator();
		while (iterator.hasNext()) {
			ShippingItem shippingItem = iterator.next();
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem != null) {
				Product product = orderItem.getProduct();
				this.productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				if (product != null) {
					if (product.getStock() != null) {
						product.setStock(Integer.valueOf(product.getStock()
								.intValue()
								- orderItem.getQuantity().intValue()));
						if (order.getIsAllocatedStock().booleanValue())
							product.setAllocatedStock(Integer.valueOf(product
									.getAllocatedStock().intValue()
									- orderItem.getQuantity().intValue()));
					}
					this.productDao.merge(product);
					this.orderDao.flush();
					this.staticService.build(product);
				}
				this.orderItemDao.lock(orderItem,
						LockModeType.PESSIMISTIC_WRITE);
				orderItem.setShippedQuantity(Integer.valueOf(orderItem
						.getShippedQuantity().intValue()
						+ orderItem.getQuantity().intValue()));
			}
		}

		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setShippingStatus(OrderShippingStatus.shipped);
			order.setIsAllocatedStock(Boolean.valueOf(false));
		} else if (order.getShippedQuantity() > 0) {
			order.setShippingStatus(OrderShippingStatus.partialShipment);
		}

		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.shipping);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);

		this.orderLogDao.persist(orderLog);
	}

	public void returns(com.korres.entity.Order order, Returns returns,
			Admin operator) {
		Assert.notNull(order);
		Assert.notNull(returns);
		Assert.notEmpty(returns.getReturnsItems());
		this.orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		returns.setOrder(order);
		this.returnsDao.persist(returns);

		Iterator<ReturnsItem> iterator = returns.getReturnsItems().iterator();
		while (iterator.hasNext()) {
			ReturnsItem returnsItem = iterator.next();
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem != null) {
				this.orderItemDao.lock(orderItem,
						LockModeType.PESSIMISTIC_WRITE);
				orderItem.setReturnQuantity(Integer.valueOf(orderItem
						.getReturnQuantity().intValue()
						+ returnsItem.getQuantity().intValue()));
			}
		}

		if (order.getReturnQuantity() >= order.getShippedQuantity()) {
			order.setShippingStatus(OrderShippingStatus.returned);
		} else if (order.getReturnQuantity() > 0) {
			order.setShippingStatus(OrderShippingStatus.partialReturns);
		}

		order.setExpire(null);
		this.orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLogType.returns);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);

		this.orderLogDao.persist(orderLog);
	}

	public void delete(com.korres.entity.Order order) {
		if (order.getIsAllocatedStock().booleanValue()) {
			Iterator<OrderItem> iterator = order.getOrderItems().iterator();
			while (iterator.hasNext()) {
				OrderItem orderItem = iterator.next();
				if (orderItem != null) {
					Product product = orderItem.getProduct();
					this.productDao.lock(product,
							LockModeType.PESSIMISTIC_WRITE);
					if ((product != null) && (product.getStock() != null)) {
						product
								.setAllocatedStock(Integer
										.valueOf(product.getAllocatedStock()
												.intValue()
												- (orderItem.getQuantity()
														.intValue() - orderItem
														.getShippedQuantity()
														.intValue())));
						this.productDao.merge(product);
						this.orderDao.flush();
						this.staticService.build(product);
					}
				}
			}
		}

		super.delete(order);
	}
}