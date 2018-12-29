package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.service.ReturnsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminReturnsController")
@RequestMapping( { "/admin/returns" })
public class ReturnsController extends BaseController {

	@Resource(name = "returnsServiceImpl")
	private ReturnsService returnsService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("returns", this.returnsService.find(id));

		return "/admin/returns/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.returnsService.findPage(pageable));

		return "/admin/returns/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.returnsService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}