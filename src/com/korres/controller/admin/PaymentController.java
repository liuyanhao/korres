package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.Payment;
import com.korres.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminPaymentController")
@RequestMapping( { "/admin/payment" })
public class PaymentController extends BaseController {

	@Resource(name = "paymentServiceImpl")
	private PaymentService paymentService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("payment", this.paymentService.find(id));

		return "/admin/payment/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.paymentService.findPage(pageable));

		return "/admin/payment/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long localLong : ids) {
				Payment localPayment = (Payment) this.paymentService
						.find(localLong);
				if ((localPayment != null)
						&& (localPayment.getExpire() != null)
						&& (!localPayment.hasExpired())) {
					return Message.error(
							"admin.payment.deleteUnexpiredNotAllowed",
							new Object[0]);
				}
			}

			this.paymentService.delete(ids);
		}

		return ADMIN_MESSAGE_SUCCESS;
	}
}