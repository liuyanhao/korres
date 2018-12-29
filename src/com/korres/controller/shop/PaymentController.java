package com.korres.controller.shop;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.entity.Member;
import com.korres.entity.Order;
import com.korres.entity.Order.OrderPaymentStatus;
import com.korres.entity.Payment;
import com.korres.entity.Payment.PaymentStatus;
import com.korres.entity.Payment.PaymentType;
import com.korres.entity.PaymentMethod.PaymentMethodType;
import com.korres.entity.Sn.SnType;
import com.korres.plugin.PaymentPlugin;
import com.korres.service.MemberService;
import com.korres.service.OrderService;
import com.korres.service.PaymentService;
import com.korres.service.PluginService;
import com.korres.service.SnService;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("shopPaymentController")
@RequestMapping({ "/payment" })
public class PaymentController extends BaseController {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;

	@Resource(name = "paymentServiceImpl")
	private PaymentService paymentService;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@RequestMapping(value = { "/submit" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String submit(String sn, String paymentPluginId,
			HttpServletRequest request, ModelMap model) {
		Order order = this.orderService.findBySn(sn);
		if (order == null) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if ((member == null) || (order.getMember() != member)
				|| (order.isExpired())) {
			return SHOP_COMMON_ERROR;
		}

		if ((order.getPaymentMethod() == null)
				|| (order.getPaymentMethod().getType() == PaymentMethodType.offline)) {
			return SHOP_COMMON_ERROR;
		}

		if ((order.getPaymentStatus() != OrderPaymentStatus.unpaid)
				&& (order.getPaymentStatus() != OrderPaymentStatus.partialPayment)) {
			return SHOP_COMMON_ERROR;
		}

		if (order.getAmountPayable().compareTo(new BigDecimal(0)) <= 0) {
			return SHOP_COMMON_ERROR;
		}

		PaymentPlugin paymentPlugin = this.pluginService
				.getPaymentPlugin(paymentPluginId);
		if ((paymentPlugin == null) || (!paymentPlugin.getIsEnabled())) {
			return SHOP_COMMON_ERROR;
		}

		BigDecimal amount = paymentPlugin.getFee(order.getAmountPayable());
		BigDecimal augend = order.getAmountPayable().add(amount);
		Payment payment = new Payment();
		payment.setSn(this.snService.generate(SnType.payment));
		payment.setType(PaymentType.online);
		payment.setStatus(PaymentStatus.wait);
		payment.setPaymentMethod(order.getPaymentMethodName() + "-"
				+ paymentPlugin.getPaymentName());
		payment.setFee(amount);
		payment.setAmount(augend);
		payment.setPaymentPluginId(paymentPluginId);
		payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils
				.addMinutes(new Date(), paymentPlugin.getTimeout().intValue())
				: null);
		payment.setMember(null);
		payment.setOrder(order);
		this.paymentService.save(payment);
		model.addAttribute("url", paymentPlugin.getUrl());
		model.addAttribute("method", paymentPlugin.getMethod());
		model.addAttribute(
				"parameterMap",
				paymentPlugin.getParameterMap(payment.getSn(), augend,
						order.getProductName(), request));

		return "shop/payment/submit";
	}

	@RequestMapping({ "/return/{sn}" })
	public String returns(@PathVariable String sn, HttpServletRequest request,
			ModelMap model) {
		Payment payment = this.paymentService.findBySn(sn);
		if (payment == null) {
			return SHOP_COMMON_ERROR;
		}

		if (payment.getStatus() == PaymentStatus.wait) {
			PaymentPlugin paymentPlugin = this.pluginService
					.getPaymentPlugin(payment.getPaymentPluginId());
			if ((paymentPlugin != null) && (paymentPlugin.verify(sn, request))) {
				BigDecimal localBigDecimal1 = paymentPlugin.getAmount(sn,
						request);
				if (localBigDecimal1.compareTo(payment.getAmount()) >= 0) {
					Order localOrder = payment.getOrder();
					if (localOrder != null) {
						if (localBigDecimal1.compareTo(localOrder
								.getAmountPayable()) >= 0)
							this.orderService
									.payment(localOrder, payment, null);
					} else {
						Member member = payment.getMember();
						if (member != null) {
							BigDecimal fee = payment.getAmount().subtract(
									payment.getFee());
							this.memberService.update(
									member,
									null,
									fee,
									getMessage("shop.payment.paymentName",
											new Object[] { paymentPlugin
													.getPaymentName() }), null);
						}
					}
				}

				payment.setStatus(PaymentStatus.success);
				payment.setAmount(localBigDecimal1);
				payment.setPaymentDate(new Date());
			} else {
				payment.setStatus(PaymentStatus.failure);
				payment.setPaymentDate(new Date());
			}
			this.paymentService.update(payment);
		}
		model.addAttribute("payment", payment);

		return "shop/payment/return";
	}

	@RequestMapping({ "/notify/{sn}" })
	public String notify(@PathVariable String sn, HttpServletRequest request,
			ModelMap model) {
		Payment payment = this.paymentService.findBySn(sn);
		if (payment != null) {
			PaymentPlugin paymentPlugin = this.pluginService
					.getPaymentPlugin(payment.getPaymentPluginId());
			if (paymentPlugin != null) {
				if ((payment.getStatus() == PaymentStatus.wait)
						&& (paymentPlugin.verify(sn, request))) {
					BigDecimal amount = paymentPlugin.getAmount(sn, request);
					if (amount.compareTo(payment.getAmount()) >= 0) {
						Order order = payment.getOrder();
						if (order != null) {
							if (amount.compareTo(order.getAmountPayable()) >= 0) {
								this.orderService.payment(order, payment, null);
							}
						} else {
							Member member = payment.getMember();
							if (member != null) {
								BigDecimal fee = payment.getAmount().subtract(
										payment.getFee());
								this.memberService.update(
										member,
										null,
										fee,
										getMessage("shop.payment.paymentName",
												new Object[] { paymentPlugin
														.getPaymentName() }),
										null);
							}
						}
					}

					payment.setStatus(PaymentStatus.success);
					payment.setAmount(amount);
					payment.setPaymentDate(new Date());
					this.paymentService.update(payment);
				}

				model.addAttribute("notifyContext",
						paymentPlugin.getNotifyContext(sn, request));
			}
		}

		return "shop/payment/notify";
	}
}