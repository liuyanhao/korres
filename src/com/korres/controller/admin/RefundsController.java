package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.service.RefundsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminRefundsController")
@RequestMapping( { "/admin/refunds" })
public class RefundsController extends BaseController {

	@Resource(name = "refundsServiceImpl")
	private RefundsService refundsService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("refunds", this.refundsService.find(id));

		return "/admin/refunds/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.refundsService.findPage(pageable));

		return "/admin/refunds/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.refundsService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}