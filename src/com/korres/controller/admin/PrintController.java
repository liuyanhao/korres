package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.DeliveryCenter;
import com.korres.entity.DeliveryTemplate;
import com.korres.service.DeliveryCenterService;
import com.korres.service.DeliveryTemplateService;
import com.korres.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminPrintController")
@RequestMapping( { "/admin/print" })
public class PrintController extends BaseController {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "deliveryTemplateServiceImpl")
	private DeliveryTemplateService deliveryTemplateService;

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@RequestMapping(value = { "/order" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String order(Long id, ModelMap model) {
		model.addAttribute("order", this.orderService.find(id));

		return "/admin/print/order";
	}

	@RequestMapping(value = { "/product" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String product(Long id, ModelMap model) {
		model.addAttribute("order", this.orderService.find(id));

		return "/admin/print/product";
	}

	@RequestMapping(value = { "/shipping" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String shipping(Long id, ModelMap model) {
		model.addAttribute("order", this.orderService.find(id));

		return "/admin/print/shipping";
	}

	@RequestMapping(value = { "/delivery" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String delivery(Long orderId, Long deliveryTemplateId,
			Long deliveryCenterId, ModelMap model) {
		DeliveryTemplate deliveryTemplate = this.deliveryTemplateService
				.find(deliveryTemplateId);
		DeliveryCenter deliveryCenter = (DeliveryCenter) this.deliveryCenterService
				.find(deliveryCenterId);
		if (deliveryTemplate == null) {
			deliveryTemplate = this.deliveryTemplateService.findDefault();
		}

		if (deliveryCenter == null) {
			deliveryCenter = this.deliveryCenterService.findDefault();
		}

		model.addAttribute("deliveryTemplates", this.deliveryTemplateService
				.findAll());
		model.addAttribute("deliveryCenters", this.deliveryCenterService
				.findAll());
		model.addAttribute("order", this.orderService.find(orderId));
		model.addAttribute("deliveryTemplate", deliveryTemplate);
		model.addAttribute("deliveryCenter", deliveryCenter);

		return "/admin/print/delivery";
	}
}