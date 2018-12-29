package com.korres.controller.shop.member;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.entity.Payment;
import com.korres.entity.Payment.PaymentStatus;
import com.korres.entity.Payment.PaymentType;
import com.korres.entity.Sn.SnType;
import com.korres.plugin.PaymentPlugin;
import com.korres.service.DepositService;
import com.korres.service.MemberService;
import com.korres.service.PaymentService;
import com.korres.service.PluginService;
import com.korres.service.SnService;
import com.korres.util.SettingUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;
import com.korres.Setting;

/*
 * 类名：DepositController.java
 * 功能说明：预存款
 * 创建日期：2018-12-20 下午04:52:57
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Controller("shopMemberDepositController")
@RequestMapping( { "/member/deposit" })
public class DepositController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "depositServiceImpl")
	private DepositService depositService;

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;

	@Resource(name = "paymentServiceImpl")
	private PaymentService paymentService;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	/**
	 * 预存款充值
	 * @param model
	 * @return
	 * @author liuxicai
	 * @date 2018-12-20 下午04:53:47
	 */
	@RequestMapping(value = { "/recharge" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String recharge(ModelMap model) {
		List<PaymentPlugin> lipp = this.pluginService.getPaymentPlugins(true);
		if (!lipp.isEmpty()) {
			model.addAttribute("defaultPaymentPlugin", lipp.get(0));
			model.addAttribute("paymentPlugins", lipp);
		}

		return "shop/member/deposit/recharge";
	}

	/**
	 * 预存款充值
	 * @param amount
	 * @param paymentPluginId
	 * @param request
	 * @param model
	 * @return
	 * @author liuxicai
	 * @date 2018-12-20 下午04:53:58
	 */
	@RequestMapping(value = { "/recharge" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String recharge(BigDecimal amount, String paymentPluginId,
			HttpServletRequest request, ModelMap model) {
		PaymentPlugin paymentPlugin = this.pluginService
				.getPaymentPlugin(paymentPluginId);
		if ((paymentPlugin == null) || (!paymentPlugin.getIsEnabled())) {
			return SHOP_COMMON_ERROR;
		}

		Setting setting = SettingUtils.get();
		if ((amount == null) || (amount.compareTo(new BigDecimal(0)) <= 0)
				|| (amount.precision() > 15)
				|| (amount.scale() > setting.getPriceScale().intValue())) {
			return SHOP_COMMON_ERROR;
		}

		BigDecimal fee = paymentPlugin.getFee(amount);
		amount = amount.add(fee);
		Payment payment = new Payment();
		payment.setSn(this.snService.generate(SnType.payment));
		payment.setType(PaymentType.online);
		payment.setStatus(PaymentStatus.wait);
		payment.setPaymentMethod(paymentPlugin.getPaymentName());
		payment.setFee(fee);
		payment.setAmount(amount);
		payment.setPaymentPluginId(paymentPluginId);
		payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils
				.addMinutes(new Date(), paymentPlugin.getTimeout().intValue())
				: null);
		payment.setMember(this.memberService.getCurrent());
		this.paymentService.save(payment);
		model.addAttribute("url", paymentPlugin.getUrl());
		model.addAttribute("method", paymentPlugin.getMethod());
		model.addAttribute("parameterMap",
				paymentPlugin
						.getParameterMap(payment.getSn(), amount, getMessage(
								"shop.member.deposit.recharge", new Object[0]),
								request));

		return "shop/payment/submit";
	}

	/**
	 * 预存款列表
	 * @param pageNumber
	 * @param model
	 * @return
	 * @author liuxicai
	 * @date 2018-12-20 下午04:53:02
	 */
	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.depositService.findPage(member,
				pageable));

		return "shop/member/deposit/list";
	}
}