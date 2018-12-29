package com.korres.controller.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Admin;
import com.korres.entity.Area;
import com.korres.entity.DeliveryCorp;
import com.korres.entity.Member;
import com.korres.entity.Order;
import com.korres.entity.OrderItem;
import com.korres.entity.Payment;
import com.korres.entity.PaymentMethod;
import com.korres.entity.Product;
import com.korres.entity.Refunds;
import com.korres.entity.Returns;
import com.korres.entity.ReturnsItem;
import com.korres.entity.Shipping;
import com.korres.entity.ShippingItem;
import com.korres.entity.ShippingMethod;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Order.OrderShippingStatus;
import com.korres.entity.Payment.PaymentStatus;
import com.korres.entity.Payment.PaymentType;
import com.korres.entity.Refunds.RefundsType;
import com.korres.entity.Sn.SnType;
import com.korres.service.AdminService;
import com.korres.service.AreaService;
import com.korres.service.DeliveryCorpService;
import com.korres.service.OrderItemService;
import com.korres.service.OrderService;
import com.korres.service.PaymentMethodService;
import com.korres.service.ProductService;
import com.korres.service.ShippingMethodService;
import com.korres.service.SnService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminOrderController")
@RequestMapping( { "/admin/order" })
public class OrderController extends BaseController {

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "orderItemServiceImpl")
	private OrderItemService orderItemService;

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@RequestMapping(value = { "/check_lock" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message checkLock(Long id) {
		Order order = this.orderService.find(id);
		if (order == null){
			return Message.warn("admin.common.invalid", new Object[0]);
		}
		
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			if (order.getOperator() != null){
				return Message
						.warn("admin.order.adminLocked",
								new Object[] { order.getOperator()
										.getUsername() });
			}
			
			return Message.warn("admin.order.memberLocked", new Object[0]);
		}
		
		order.setLockExpire(DateUtils.addSeconds(new Date(), 60));
		order.setOperator(admin);
		this.orderService.update(order);
		
		return ADMIN_MESSAGE_SUCCESS;
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("types", PaymentType.values());
		model.addAttribute("refundsTypes", RefundsType.values());
		model.addAttribute("paymentMethods", this.paymentMethodService
				.findAll());
		model.addAttribute("shippingMethods", this.shippingMethodService
				.findAll());
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		
		return "/admin/order/view";
	}

	@RequestMapping(value = { "/confirm" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String confirm(Long id, RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if ((order != null)
				&& (!order.isExpired())
				&& (order.getOrderStatus() == OrderOrderStatus.unconfirmed)
				&& (!order.isLocked(admin))) {
			this.orderService.confirm(order, admin);
			setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		} else {
			setRedirectAttributes(redirectAttributes, Message.warn("admin.common.invalid",
					new Object[0]));
		}
		
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/complete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String complete(Long id, RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if ((order != null) && (!order.isExpired())
				&& (order.getOrderStatus() == OrderOrderStatus.confirmed)
				&& (!order.isLocked(admin))) {
			this.orderService.complete(order, admin);
			setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		} else {
			setRedirectAttributes(redirectAttributes, Message.warn("admin.common.invalid",
					new Object[0]));
		}
		
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/cancel" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String cancel(Long id, RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if ((order != null)
				&& (!order.isExpired())
				&& (order.getOrderStatus() == OrderOrderStatus.unconfirmed)
				&& (!order.isLocked(admin))) {
			this.orderService.cancel(order, admin);
			setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		} else {
			setRedirectAttributes(redirectAttributes, Message.warn("admin.common.invalid",
					new Object[0]));
		}
		
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/payment" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String payment(Long orderId, Long paymentMethodId, Payment payment,
			RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(orderId);
		payment.setOrder(order);
		PaymentMethod localPaymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		payment
				.setPaymentMethod(localPaymentMethod != null ? localPaymentMethod
						.getName()
						: null);
		
		if (!validator(payment, new Class[0])){
			return "/admin/common/error";
		}
		
		if ((order.isExpired())
				|| (order.getOrderStatus() != OrderOrderStatus.confirmed)){
			return "/admin/common/error";
		}
		
		if ((order.getPaymentStatus() != OrderPaymentStatus.unpaid)
				&& (order.getPaymentStatus() != OrderPaymentStatus.partialPayment)){
			return "/admin/common/error";
		}
		
		if ((payment.getAmount().compareTo(new BigDecimal(0)) <= 0)
				|| (payment.getAmount()
						.compareTo(order.getAmountPayable()) > 0)){
			return "/admin/common/error";
		}
		
		Member localMember = order.getMember();
		if ((payment.getType() == PaymentType.deposit)
				&& (payment.getAmount().compareTo(localMember.getBalance()) > 0)){
			return "/admin/common/error";
		}
		
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)){
			return "/admin/common/error";
		}
		
		payment.setSn(this.snService.generate(SnType.payment));
		payment.setStatus(PaymentStatus.success);
		payment.setFee(new BigDecimal(0));
		payment.setOperator(admin.getUsername());
		payment.setPaymentDate(new Date());
		payment.setPaymentPluginId(null);
		payment.setExpire(null);
		payment.setDeposit(null);
		payment.setMember(null);
		this.orderService.payment(order, payment, admin);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/refunds" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String refunds(Long orderId, Long paymentMethodId, Refunds refunds,
			RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(orderId);
		refunds.setOrder(order);
		PaymentMethod localPaymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		refunds
				.setPaymentMethod(localPaymentMethod != null ? localPaymentMethod
						.getName()
						: null);
		
		if (!validator(refunds, new Class[0])){
			return "/admin/common/error";
		}
		
		if ((order.isExpired())
				|| (order.getOrderStatus() != OrderOrderStatus.confirmed)){
			return "/admin/common/error";
		}
		
		if ((order.getPaymentStatus() != OrderPaymentStatus.paid)
				&& (order.getPaymentStatus() != OrderPaymentStatus.partialPayment)
				&& (order.getPaymentStatus() != OrderPaymentStatus.partialRefunds)){
			return "/admin/common/error";
		}
			
		if ((refunds.getAmount().compareTo(new BigDecimal(0)) <= 0)
				|| (refunds.getAmount().compareTo(order.getAmountPaid()) > 0)){
			return "/admin/common/error";
		}
		
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)){
			return "/admin/common/error";
		}
		refunds.setSn(this.snService.generate(SnType.refunds));
		refunds.setOperator(admin.getUsername());
		this.orderService.refunds(order, refunds, admin);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/shipping" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String shipping(Long orderId, Long shippingMethodId,
			Long deliveryCorpId, Long areaId, Shipping shipping,
			RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(orderId);
		if (order == null){
			return "/admin/common/error";
		}
		
		Iterator<ShippingItem> iterator = shipping.getShippingItems()
				.iterator();
		while (iterator.hasNext()) {
			ShippingItem shippingItem = (ShippingItem) iterator.next();
			if ((shippingItem == null)
					|| (StringUtils.isEmpty(shippingItem.getSn()))
					|| (shippingItem.getQuantity() == null)
					|| (shippingItem.getQuantity().intValue() <= 0)) {
				iterator.remove();
			} else {
				OrderItem orderItem = order.getOrderItem(shippingItem
						.getSn());
				if ((orderItem == null)
						|| shippingItem.getQuantity().intValue() > (orderItem
								.getQuantity().intValue() - orderItem
								.getShippedQuantity().intValue())){
					return "/admin/common/error";
				}
				
				if ((orderItem.getProduct() != null)
						&& (orderItem.getProduct().getStock() != null)
						&& shippingItem.getQuantity().intValue() > (orderItem
								.getProduct().getStock().intValue())){
					return "/admin/common/error";
				}
				
				shippingItem.setName(orderItem.getFullName());
				shippingItem.setShipping(shipping);
			}
		}

		shipping.setOrder(order);
		ShippingMethod shippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		shipping.setShippingMethod(shippingMethod != null ? shippingMethod
				.getName() : null);
		Object localObject2 = (DeliveryCorp) this.deliveryCorpService
				.find(deliveryCorpId);
		shipping
				.setDeliveryCorp(localObject2 != null ? ((DeliveryCorp) localObject2)
						.getName()
						: null);
		shipping
				.setDeliveryCorpUrl(localObject2 != null ? ((DeliveryCorp) localObject2)
						.getUrl()
						: null);
		shipping
				.setDeliveryCorpCode(localObject2 != null ? ((DeliveryCorp) localObject2)
						.getCode()
						: null);
		Object localObject3 = (Area) this.areaService.find(areaId);
		shipping.setArea(localObject3 != null ? ((Area) localObject3)
				.getFullName() : null);

		if (!validator(shipping, new Class[0])) {
			return "/admin/common/error";
		}

		if ((order.isExpired())
				|| (order.getOrderStatus() != OrderOrderStatus.confirmed)) {
			return "/admin/common/error";
		}

		if ((order.getShippingStatus() != OrderShippingStatus.unshipped)
				&& (order.getShippingStatus() != OrderShippingStatus.partialShipment)) {
			return "/admin/common/error";
		}

		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}

		shipping.setSn(this.snService.generate(SnType.shipping));
		shipping.setOperator(admin.getUsername());
		this.orderService.shipping(order, shipping, admin);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/returns" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String returns(Long orderId, Long shippingMethodId,
			Long deliveryCorpId, Long areaId, Returns returns,
			RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(orderId);

		if (order == null) {
			return "/admin/common/error";
		}

		Iterator<ReturnsItem> iterator = returns.getReturnsItems().iterator();
		while (iterator.hasNext()) {
			ReturnsItem returnsItem = iterator.next();
			if ((returnsItem == null)
					|| (StringUtils.isEmpty(returnsItem.getSn()))
					|| (returnsItem.getQuantity() == null)
					|| (returnsItem.getQuantity().intValue() <= 0)) {
				iterator.remove();
			} else {
				OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
				if ((orderItem == null)
						|| (returnsItem.getQuantity().intValue() > orderItem
								.getShippedQuantity().intValue()
								- orderItem.getReturnQuantity().intValue()))
					return "/admin/common/error";
				returnsItem.setName(orderItem.getFullName());
				returnsItem.setReturns(returns);
			}
		}

		returns.setOrder(order);
		ShippingMethod shippingMethod = this.shippingMethodService
				.find(shippingMethodId);
		returns.setShippingMethod(shippingMethod != null ? shippingMethod
				.getName() : null);
		DeliveryCorp deliveryCorp = (DeliveryCorp) this.deliveryCorpService
				.find(deliveryCorpId);
		returns.setDeliveryCorp(deliveryCorp != null ? deliveryCorp.getName()
				: null);
		Area area = this.areaService.find(areaId);
		returns.setArea(area != null ? area.getFullName() : null);
		if (!validator(returns, new Class[0])) {
			return "/admin/common/error";
		}

		if ((order.isExpired())
				|| (order.getOrderStatus() != OrderOrderStatus.confirmed)) {
			return "/admin/common/error";
		}

		if ((order.getShippingStatus() != OrderShippingStatus.shipped)
				&& (order.getShippingStatus() != OrderShippingStatus.partialShipment)
				&& (order.getShippingStatus() != OrderShippingStatus.partialReturns)) {
			return "/admin/common/error";
		}

		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}

		returns.setSn(this.snService.generate(SnType.returns));
		returns.setOperator(admin.getUsername());
		this.orderService.returns(order, returns, admin);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("paymentMethods", this.paymentMethodService
				.findAll());
		model.addAttribute("shippingMethods", this.shippingMethodService
				.findAll());
		model.addAttribute("order", this.orderService.find(id));

		return "/admin/order/edit";
	}

	@RequestMapping(value = { "/order_item_add" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> orderItemAdd(String productSn) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Product product = this.productService.findBySn(productSn);
		if (product == null) {
			map.put("message", Message.warn("admin.order.productNotExist",
					new Object[0]));
			return map;
		}
		
		if (!product.getIsMarketable().booleanValue()) {
			map.put("message", Message.warn("admin.order.productNotMarketable",
					new Object[0]));
			return map;
		}
		
		if (product.getIsOutOfStock().booleanValue()) {
			map.put("message", Message.warn("admin.order.productOutOfStock",
					new Object[0]));
			return map;
		}
		
		map.put("sn", product.getSn());
		map.put("fullName", product.getFullName());
		map.put("price", product.getPrice());
		map.put("weight", product.getWeight());
		map.put("isGift", product.getIsGift());
		map.put("message", ADMIN_MESSAGE_SUCCESS);

		return map;
	}

	@RequestMapping(value = { "/calculate" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> calculate(Order order, Long areaId,
			Long paymentMethodId, Long shippingMethodId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<OrderItem> iterator = order.getOrderItems().iterator();
		while (iterator.hasNext()) {
			OrderItem orderItem = iterator.next();
			if ((orderItem == null) || (StringUtils.isEmpty(orderItem.getSn())))
				iterator.remove();
		}
		order.setArea(this.areaService.find(areaId));
		order.setPaymentMethod(this.paymentMethodService.find(paymentMethodId));
		order.setShippingMethod(this.shippingMethodService
				.find(shippingMethodId));
		if (!validator(order, new Class[0])) {
			map.put("message", Message.warn("admin.common.invalid",
					new Object[0]));
			return map;
		}
		Order or = this.orderService.find(order.getId());
		if (or == null) {
			map.put("message", Message.error("admin.common.invalid",
					new Object[0]));
			return map;
		}

		Iterator<OrderItem> iterator2 = order.getOrderItems().iterator();
		while (iterator2.hasNext()) {
			OrderItem orderItem = iterator2.next();
			if (orderItem.getId() != null) {
				OrderItem orderItem2 = this.orderItemService.find(orderItem
						.getId());
				if ((orderItem2 == null)
						|| (!order.equals(orderItem2.getOrder()))) {
					map.put("message", Message.error("admin.common.invalid",
							new Object[0]));
					return map;
				}
				Product product = orderItem2.getProduct();
				if ((product != null) && (product.getStock() != null))
					if (order.getIsAllocatedStock().booleanValue()) {
						if (orderItem.getQuantity().intValue() > product
								.getAvailableStock().intValue()
								+ orderItem2.getQuantity().intValue()) {
							map.put("message", Message.warn(
									"admin.order.lowStock", new Object[0]));
							return map;
						}
					} else if (orderItem.getQuantity().intValue() > product
							.getAvailableStock().intValue()) {
						map.put("message", Message.warn("admin.order.lowStock",
								new Object[0]));
						return map;
					}
			} else {
				Product product = this.productService.findBySn(orderItem
						.getSn());
				if (product == null) {
					map.put("message", Message.error("admin.common.invalid",
							new Object[0]));
					return map;
				}
				if ((product.getStock() != null)
						&& (orderItem.getQuantity().intValue() > product
								.getAvailableStock().intValue())) {
					map.put("message", Message.warn("admin.order.lowStock",
							new Object[0]));
					return map;
				}
			}
		}

		Map<String, Object> m = new HashMap<String, Object>();
		Iterator<OrderItem> iterator3 = order.getOrderItems().iterator();
		while (iterator3.hasNext()) {
			OrderItem orderItem = (OrderItem) iterator3.next();
			m.put(orderItem.getSn(), orderItem);
		}

		map.put("weight", Integer.valueOf(order.getWeight()));
		map.put("price", order.getPrice());
		map.put("quantity", Integer.valueOf(order.getQuantity()));
		map.put("amount", order.getAmount());
		map.put("orderItems", m);
		map.put("message", ADMIN_MESSAGE_SUCCESS);

		return map;
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Order order, Long areaId, Long paymentMethodId,
			Long shippingMethodId, RedirectAttributes redirectAttributes) {
		Iterator<OrderItem> iterator = order.getOrderItems().iterator();
		while (iterator.hasNext()) {
			OrderItem orderItem = (OrderItem) iterator.next();
			if ((orderItem == null) || (StringUtils.isEmpty(orderItem.getSn()))) {
				iterator.remove();
			}
		}
		
		order.setArea(this.areaService.find(areaId));
		order.setPaymentMethod(this.paymentMethodService.find(paymentMethodId));
		order.setShippingMethod(this.shippingMethodService
				.find(shippingMethodId));

		if (!validator(order, new Class[0])) {
			return "/admin/common/error";
		}

		Order or = (Order) this.orderService.find(order.getId());
		if (or == null) {
			return "/admin/common/error";
		}

		if ((or.isExpired())
				|| (or.getOrderStatus() != OrderOrderStatus.unconfirmed)) {
			return "/admin/common/error";
		}

		Admin admin = this.adminService.getCurrent();
		if (or.isLocked(admin)) {
			return "/admin/common/error";
		}

		if (!order.getIsInvoice().booleanValue()) {
			order.setInvoiceTitle(null);
			order.setTax(new BigDecimal(0));
		}

		Iterator<OrderItem> iterator2 = order.getOrderItems().iterator();
		while (iterator2.hasNext()) {
			OrderItem orderItem = (OrderItem) iterator2.next();
			if (orderItem.getId() != null) {
				OrderItem orderItem2 = this.orderItemService.find(orderItem
						.getId());
				if ((orderItem2 == null) || (!or.equals(orderItem2.getOrder()))) {
					return "/admin/common/error";
				}

				Product product = orderItem2.getProduct();
				if ((product != null) && (product.getStock() != null)) {
					if (or.getIsAllocatedStock().booleanValue()) {
						if (orderItem.getQuantity().intValue() > product
								.getAvailableStock().intValue()
								+ orderItem2.getQuantity().intValue()) {
							return "/admin/common/error";
						}
					} else if (orderItem.getQuantity().intValue() > product
							.getAvailableStock().intValue()) {
						return "/admin/common/error";
					}
				}

				BeanUtils.copyProperties(orderItem2, orderItem, new String[] {
						"price", "quantity" });
				if (orderItem2.getIsGift().booleanValue()) {
					orderItem.setPrice(new BigDecimal(0));
				}
			} else {
				Product product = this.productService.findBySn(orderItem
						.getSn());
				if (product == null) {
					return "/admin/common/error";
				}

				if ((product.getStock() != null)
						&& (orderItem.getQuantity().intValue() > product
								.getAvailableStock().intValue())) {
					return "/admin/common/error";
				}

				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				if (product.getIsGift().booleanValue()) {
					orderItem.setPrice(new BigDecimal(0));
				}
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(product.getIsGift());
				orderItem.setShippedQuantity(Integer.valueOf(0));
				orderItem.setReturnQuantity(Integer.valueOf(0));
				orderItem.setProduct(product);
				orderItem.setOrder(or);
			}
		}

		order.setSn(or.getSn());
		order.setOrderStatus(or.getOrderStatus());
		order.setPaymentStatus(or.getPaymentStatus());
		order.setShippingStatus(or.getShippingStatus());
		order.setFee(or.getFee());
		order.setAmountPaid(or.getAmountPaid());
		order.setPromotion(or.getPromotion());
		order.setExpire(or.getExpire());
		order.setLockExpire(null);
		order.setIsAllocatedStock(or.getIsAllocatedStock());
		order.setOperator(null);
		order.setMember(or.getMember());
		order.setCouponCode(or.getCouponCode());
		order.setCoupons(or.getCoupons());
		order.setOrderLogs(or.getOrderLogs());
		order.setDeposits(or.getDeposits());
		order.setPayments(or.getPayments());
		order.setRefunds(or.getRefunds());
		order.setShippings(or.getShippings());
		order.setReturns(or.getReturns());
		this.orderService.update(order, admin);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(OrderOrderStatus orderStatus,
			OrderPaymentStatus paymentStatus,
			OrderShippingStatus shippingStatus, Boolean hasExpired,
			Pageable pageable, ModelMap model) {
		model.addAttribute("orderStatus", orderStatus);
		model.addAttribute("paymentStatus", paymentStatus);
		model.addAttribute("shippingStatus", shippingStatus);
		model.addAttribute("hasExpired", hasExpired);
		model.addAttribute("page", this.orderService.findPage(orderStatus,
				paymentStatus, shippingStatus, hasExpired, pageable));
		
		return "/admin/order/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			Admin admin = this.adminService.getCurrent();
			for (Long id : ids) {
				Order localOrder = (Order) this.orderService.find(id);
				if ((localOrder != null) && (localOrder.isLocked(admin)))
					return Message.error("admin.order.deleteLockedNotAllowed",
							new Object[] { localOrder.getSn() });
			}
			this.orderService.delete(ids);
		}
		return ADMIN_MESSAGE_SUCCESS;
	}
}