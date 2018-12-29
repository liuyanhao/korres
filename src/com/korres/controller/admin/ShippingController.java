package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.service.ShippingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminShippingController")
@RequestMapping({ "/admin/shipping" })
public class ShippingController extends BaseController {

	@Resource(name = "shippingServiceImpl")
	private ShippingService shippingService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("shipping", this.shippingService.find(id));
		
		return "/admin/shipping/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.shippingService.findPage(pageable));
		
		return "/admin/shipping/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.shippingService.delete(ids);
		
		return ADMIN_MESSAGE_SUCCESS;
	}
}