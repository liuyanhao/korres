package com.korres.controller.shop.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.controller.shop.BaseController;
import com.korres.entity.Cart;
import com.korres.entity.Coupon;
import com.korres.entity.CouponCode;
import com.korres.entity.Member;
import com.korres.entity.Order;
import com.korres.entity.PaymentMethod;
import com.korres.entity.Receiver;
import com.korres.entity.Shipping;
import com.korres.entity.ShippingMethod;
import com.korres.entity.Order.OrderOrderStatus;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.PaymentMethod.PaymentMethodType;
import com.korres.plugin.PaymentPlugin;
import com.korres.service.AreaService;
import com.korres.service.CartService;
import com.korres.service.CouponCodeService;
import com.korres.service.MemberService;
import com.korres.service.OrderService;
import com.korres.service.PaymentMethodService;
import com.korres.service.PluginService;
import com.korres.service.ReceiverService;
import com.korres.service.ShippingMethodService;
import com.korres.service.ShippingService;
import com.korres.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;
import com.korres.Setting;

@Controller("shopMemberOrderController")
@RequestMapping( { "/member/order" })
public class OrderController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "shippingServiceImpl")
	private ShippingService shippingService;

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;

	@RequestMapping(value = { "/save_receiver" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> saveReceiver(Receiver receiver, Long areaId) {
		Map<String, Object> map = new HashMap<String, Object>();
		receiver.setArea(this.areaService.find(areaId));
		if (!validate(receiver, new Class[0])) {
			map.put("message", SHOP_MESSAGE_ERROR);
			return map;
		}
		Member member = this.memberService.getCurrent();
		if ((Receiver.MAX_RECEIVER_COUNT != null)
				&& (member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT
						.intValue())) {
			map.put("message", Message.error(
					"shop.order.addReceiverCountNotAllowed",
					new Object[] { Receiver.MAX_RECEIVER_COUNT }));
			return map;
		}

		receiver.setMember(member);
		this.receiverService.save(receiver);
		map.put("message", SHOP_MESSAGE_SUCCESS);
		map.put("receiver", receiver);

		return map;
	}

	@RequestMapping(value = { "/check_lock" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message checkLock(String sn) {
		Order order = this.orderService.findBySn(sn);
		if ((order != null)
				&& (order.getMember() == this.memberService.getCurrent())
				&& (!order.isExpired())
				&& (order.getPaymentMethod() != null)
				&& (order.getPaymentMethod().getType() == PaymentMethodType.online)
				&& ((order.getPaymentStatus() == OrderPaymentStatus.unpaid) || (order
						.getPaymentStatus() == OrderPaymentStatus.partialPayment))) {
			if (order.isLocked(null)) {
				return Message.warn("shop.order.locked", new Object[0]);
			}

			order.setLockExpire(DateUtils.addSeconds(new Date(), 60));
			order.setOperator(null);
			this.orderService.update(order);

			return SHOP_MESSAGE_SUCCESS;
		}
		return SHOP_MESSAGE_ERROR;
	}

	@RequestMapping(value = { "/check_payment" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public boolean checkPayment(String sn) {
		Order order = this.orderService.findBySn(sn);
		return (order != null)
				&& (order.getMember() == this.memberService.getCurrent())
				&& (order.getPaymentStatus() == OrderPaymentStatus.paid);
	}

	@RequestMapping(value = { "/coupon_info" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> couponInfo(String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			map.put("message", Message.warn("shop.order.cartNotEmpty",
					new Object[0]));
			return map;
		}

		if (!cart.isCouponAllowed()) {
			map.put("message", Message.warn("shop.order.couponNotAllowed",
					new Object[0]));
			return map;
		}

		CouponCode couponCode = this.couponCodeService.findByCode(code);
		if ((couponCode != null) && (couponCode.getCoupon() != null)) {
			Coupon coupon = couponCode.getCoupon();
			if (!coupon.getIsEnabled().booleanValue()) {
				map.put("message", Message.warn("shop.order.couponDisabled",
						new Object[0]));
				return map;
			}
			if (!coupon.hasBegun()) {
				map.put("message", Message.warn("shop.order.couponNotBegin",
						new Object[0]));
				return map;
			}
			if (coupon.hasExpired()) {
				map.put("message", Message.warn("shop.order.couponHasExpired",
						new Object[0]));
				return map;
			}
			if (!cart.isValid(coupon)) {
				map.put("message", Message.warn("shop.order.couponInvalid",
						new Object[0]));
				return map;
			}
			if (couponCode.getIsUsed().booleanValue()) {
				map.put("message", Message.warn("shop.order.couponCodeUsed",
						new Object[0]));
				return map;
			}

			map.put("message", SHOP_MESSAGE_SUCCESS);
			map.put("couponName", coupon.getName());

			return map;
		}

		map.put("message", Message.warn("shop.order.couponCodeNotExist",
				new Object[0]));

		return map;
	}

	@RequestMapping(value = { "/info" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String info(ModelMap model) {
		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			return "redirect:/cart/list.jhtml";
		}

		if (!validate(cart, new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		Order order = this.orderService.build(cart, null, null, null, null,
				false, null, false, null);
		model.addAttribute("order", order);
		model.addAttribute("cartToken", cart.getToken());
		model.addAttribute("paymentMethods", this.paymentMethodService
				.findAll());
		model.addAttribute("shippingMethods", this.shippingMethodService
				.findAll());

		return "/shop/member/order/info";
	}

	@RequestMapping(value = { "/calculate" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> calculate(Long paymentMethodId,
			Long shippingMethodId, String code,
			@RequestParam(defaultValue = "false") Boolean isInvoice,
			String invoiceTitle,
			@RequestParam(defaultValue = "false") Boolean useBalance,
			String memo) {
		Map<String, Object> map = new HashMap<String, Object>();
		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			map.put("message", Message.error("shop.order.cartNotEmpty",
					new Object[0]));
			return map;
		}
		PaymentMethod paymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		ShippingMethod shippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		CouponCode couponCode = this.couponCodeService.findByCode(code);
		Order localOrder = this.orderService.build(cart, null, paymentMethod,
				shippingMethod, couponCode, isInvoice.booleanValue(),
				invoiceTitle, useBalance.booleanValue(), memo);
		map.put("message", SHOP_MESSAGE_SUCCESS);
		map.put("quantity", Integer.valueOf(localOrder.getQuantity()));
		map.put("price", localOrder.getPrice());
		map.put("freight", localOrder.getFreight());
		map.put("tax", localOrder.getTax());
		map.put("amountPayable", localOrder.getAmountPayable());

		return map;
	}

	@RequestMapping(value = { "/create" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message create(String cartToken, Long receiverId,
			Long paymentMethodId, Long shippingMethodId, String code,
			@RequestParam(defaultValue = "false") Boolean isInvoice,
			String invoiceTitle,
			@RequestParam(defaultValue = "false") Boolean useBalance,
			String memo) {
		Cart cart = this.cartService.getCurrent();
		if ((cart == null) || (cart.isEmpty())) {
			return Message.warn("shop.order.cartNotEmpty", new Object[0]);
		}

		if (!StringUtils.equals(cart.getToken(), cartToken)) {
			return Message.warn("shop.order.cartHasChanged", new Object[0]);
		}

		if (cart.getIsLowStock()) {
			return Message.warn("shop.order.cartLowStock", new Object[0]);
		}

		Receiver receiver = (Receiver) this.receiverService.find(receiverId);
		if (receiver == null) {
			return Message.error("shop.order.receiverNotExsit", new Object[0]);
		}

		PaymentMethod paymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		if (paymentMethod == null) {
			return Message.error("shop.order.paymentMethodNotExsit",
					new Object[0]);
		}

		ShippingMethod shippingMethod = (ShippingMethod) this.shippingMethodService
				.find(shippingMethodId);
		if (shippingMethod == null) {
			return Message.error("shop.order.shippingMethodNotExsit",
					new Object[0]);
		}

		if (!paymentMethod.getShippingMethods().contains(shippingMethod)) {
			return Message.error("shop.order.deliveryUnsupported",
					new Object[0]);
		}

		CouponCode couponCode = this.couponCodeService.findByCode(code);
		Order order = this.orderService.create(cart, receiver, paymentMethod,
				shippingMethod, couponCode, isInvoice.booleanValue(),
				invoiceTitle, useBalance.booleanValue(), memo, null);

		return Message.success(order.getSn(), new Object[0]);
	}

	@RequestMapping(value = { "/payment" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String payment(String sn, ModelMap model) {
		Order order = this.orderService.findBySn(sn);
		if ((order == null)
				|| (order.getMember() != this.memberService.getCurrent())
				|| (order.isExpired()) || (order.getPaymentMethod() == null)) {
			return SHOP_COMMON_ERROR;
		}

		if (order.getPaymentMethod().getType() == PaymentMethodType.online) {
			List<PaymentPlugin> lipp = this.pluginService
					.getPaymentPlugins(true);
			if (!lipp.isEmpty()) {
				PaymentPlugin paymentPlugin = lipp.get(0);
				order.setFee(paymentPlugin.getFee(order.getAmountPayable()));
				model.addAttribute("defaultPaymentPlugin", paymentPlugin);
				model.addAttribute("paymentPlugins", lipp);
			}
		}

		model.addAttribute("order", order);

		return "/shop/member/order/payment";
	}

	@RequestMapping(value = { "/payment_plugin_select" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> paymentPluginSelect(String sn,
			String paymentPluginId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Order order = this.orderService.findBySn(sn);
		PaymentPlugin localPaymentPlugin = this.pluginService
				.getPaymentPlugin(paymentPluginId);
		if ((order == null)
				|| (order.getMember() != this.memberService.getCurrent())
				|| (order.isExpired())
				|| (order.isLocked(null))
				|| (order.getPaymentMethod() == null)
				|| (order.getPaymentMethod().getType() == PaymentMethodType.offline)
				|| (localPaymentPlugin == null)
				|| (!localPaymentPlugin.getIsEnabled())) {
			map.put("message", SHOP_MESSAGE_ERROR);
			return map;
		}
		order.setFee(localPaymentPlugin.getFee(order.getAmountPayable()));
		map.put("message", SHOP_MESSAGE_SUCCESS);
		map.put("fee", order.getFee());
		map.put("amountPayable", order.getAmountPayable());
		return map;
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model
				.addAttribute("page", this.orderService.findPage(member,
						pageable));

		return "shop/member/order/list";
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(String sn, ModelMap model) {
		Order order = this.orderService.findBySn(sn);
		if (order == null) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (!member.getOrders().contains(order)) {
			return SHOP_COMMON_ERROR;
		}

		model.addAttribute("order", order);

		return "shop/member/order/view";
	}

	@RequestMapping(value = { "/cancel" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message cancel(String sn) {
		Order order = this.orderService.findBySn(sn);
		if ((order != null)
				&& (order.getMember() == this.memberService.getCurrent())
				&& (!order.isExpired())
				&& (order.getOrderStatus() == OrderOrderStatus.unconfirmed)
				&& (order.getPaymentStatus() == OrderPaymentStatus.unpaid)) {
			if (order.isLocked(null)) {
				return Message.warn("shop.member.order.locked", new Object[0]);
			}

			this.orderService.cancel(order, null);

			return SHOP_MESSAGE_SUCCESS;
		}
		return SHOP_MESSAGE_ERROR;
	}

	@RequestMapping(value = { "/delivery_query" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> deliveryQuery(String sn) {
		Map<String, Object> map = new HashMap<String, Object>();
		Shipping shipping = this.shippingService.findBySn(sn);
		Setting setting = SettingUtils.get();
		if ((shipping != null)
				&& (shipping.getOrder() != null)
				&& (shipping.getOrder().getMember() == this.memberService
						.getCurrent())
				&& (StringUtils.isNotEmpty(setting.getKuaidi100Key()))
				&& (StringUtils.isNotEmpty(shipping.getDeliveryCorpCode()))
				&& (StringUtils.isNotEmpty(shipping.getTrackingNo()))) {
			map = this.shippingService.query(shipping);
		}

		return map;
	}
}