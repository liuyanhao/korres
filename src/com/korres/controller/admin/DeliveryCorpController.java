package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.DeliveryCorp;
import com.korres.service.DeliveryCorpService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminDeliveryCorpController")
@RequestMapping( { "/admin/delivery_corp" })
public class DeliveryCorpController extends BaseController {

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add() {
		return "/admin/delivery_corp/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(DeliveryCorp deliveryCorp,
			RedirectAttributes redirectAttributes) {
		if (!validator(deliveryCorp, new Class[0])) {
			return "/admin/common/error";
		}

		deliveryCorp.setShippingMethods(null);
		this.deliveryCorpService.save(deliveryCorp);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("deliveryCorp", this.deliveryCorpService.find(id));

		return "/admin/delivery_corp/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(DeliveryCorp deliveryCorp,
			RedirectAttributes redirectAttributes) {
		if (!validator(deliveryCorp, new Class[0])) {
			return "/admin/common/error";
		}

		this.deliveryCorpService.update(deliveryCorp,
				new String[] { "shippingMethods" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.deliveryCorpService.findPage(pageable));

		return "/admin/delivery_corp/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.deliveryCorpService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}