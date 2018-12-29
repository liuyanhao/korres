package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.DeliveryTemplate;
import com.korres.service.DeliveryTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminDeliveryTemplateController")
@RequestMapping( { "/admin/delivery_template" })
public class DeliveryTemplateController extends BaseController {

	@Resource(name = "deliveryTemplateServiceImpl")
	private DeliveryTemplateService deliveryTemplateService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(Pageable pageable) {
		return "/admin/delivery_template/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(DeliveryTemplate deliveryTemplate,
			RedirectAttributes redirectAttributes) {
		if (!validator(deliveryTemplate, new Class[0])) {
			return "/admin/common/error";
		}

		this.deliveryTemplateService.save(deliveryTemplate);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String eidt(Long id, Model model) {
		model.addAttribute("deliveryTemplate", this.deliveryTemplateService
				.find(id));

		return "/admin/delivery_template/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String udpate(DeliveryTemplate deliveryTemplate,
			RedirectAttributes redirectAttributes) {
		if (!validator(deliveryTemplate, new Class[0])) {
			return "/admin/common/error";
		}

		this.deliveryTemplateService.update(deliveryTemplate);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, Model model) {
		model.addAttribute("page", this.deliveryTemplateService
				.findPage(pageable));

		return "/admin/delivery_template/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.deliveryTemplateService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}