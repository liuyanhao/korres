package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.Area;
import com.korres.entity.DeliveryCenter;
import com.korres.service.AreaService;
import com.korres.service.DeliveryCenterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("deliveryCenterController")
@RequestMapping( { "/admin/delivery_center" })
public class DeliveryCenterController extends BaseController {

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add() {
		return "/admin/delivery_center/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(DeliveryCenter deliveryCenter, Long areaId, Model model,
			RedirectAttributes redirectAttributes) {
		deliveryCenter.setArea((Area) this.areaService.find(areaId));
		if (!validator(deliveryCenter, new Class[0])) {
			return "/admin/common/error";
		}

		deliveryCenter.setAreaName(null);
		this.deliveryCenterService.save(deliveryCenter);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, Model model) {
		model.addAttribute("deliveryCenter", this.deliveryCenterService
				.find(id));

		return "/admin/delivery_center/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(DeliveryCenter deliveryCenter, Long areaId,
			RedirectAttributes redirectAttributes) {
		deliveryCenter.setArea((Area) this.areaService.find(areaId));
		if (!validator(deliveryCenter, new Class[0])) {
			return "/admin/common/error";
		}

		this.deliveryCenterService.update(deliveryCenter,
				new String[] { "areaName" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Model model, Pageable pageable) {
		model.addAttribute("page", this.deliveryCenterService
				.findPage(pageable));

		return "/admin/delivery_center/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.deliveryCenterService.delete(ids);
		
		return ADMIN_MESSAGE_SUCCESS;
	}
}