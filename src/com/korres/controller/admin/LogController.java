package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.service.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminLogController")
@RequestMapping( { "/admin/log" })
public class LogController extends BaseController {

	@Resource(name = "logServiceImpl")
	private LogService logService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.logService.findPage(pageable));
		
		return "/admin/log/list";
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("log", this.logService.find(id));
		
		return "/admin/log/view";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.logService.delete(ids);
		
		return ADMIN_MESSAGE_SUCCESS;
	}

	@RequestMapping(value = { "/clear" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message clear() {
		this.logService.clear();

		return ADMIN_MESSAGE_SUCCESS;
	}
}