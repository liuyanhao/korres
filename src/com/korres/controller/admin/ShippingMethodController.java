package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.DeliveryCorp;
import com.korres.entity.ShippingMethod;
import com.korres.service.DeliveryCorpService;
import com.korres.service.ShippingMethodService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminShippingMethodController")
@RequestMapping({ "/admin/shipping_method" })
public class ShippingMethodController extends BaseController {

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		
		return "/admin/shipping_method/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(ShippingMethod shippingMethod,
			Long defaultDeliveryCorpId, RedirectAttributes redirectAttributes) {
		shippingMethod
				.setDefaultDeliveryCorp((DeliveryCorp) this.deliveryCorpService
						.find(defaultDeliveryCorpId));
		if (!validator(shippingMethod, new Class[0]))
			return "/admin/common/error";
		shippingMethod.setPaymentMethods(null);
		shippingMethod.setOrders(null);
		this.shippingMethodService.save(shippingMethod);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		model.addAttribute("shippingMethod",
				this.shippingMethodService.find(id));
		
		return "/admin/shipping_method/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(ShippingMethod shippingMethod,
			Long defaultDeliveryCorpId, RedirectAttributes redirectAttributes) {
		shippingMethod
				.setDefaultDeliveryCorp((DeliveryCorp) this.deliveryCorpService
						.find(defaultDeliveryCorpId));
		
		if (!validator(shippingMethod, new Class[0])){
			return "/admin/common/error";
		}
		
		this.shippingMethodService.update(shippingMethod, new String[] {
				"paymentMethods", "orders" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page",
				this.shippingMethodService.findPage(pageable));
		
		return "/admin/shipping_method/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids.length >= this.shippingMethodService.count())
			return Message.error("admin.common.deleteAllNotAllowed",
					new Object[0]);
		this.shippingMethodService.delete(ids);
		
		return ADMIN_MESSAGE_SUCCESS;
	}
}