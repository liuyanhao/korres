package com.korres.controller.admin;

import javax.annotation.Resource;

import com.korres.service.TemplateService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.korres.Template.TemplateType;

@Controller("adminTemplateController")
@RequestMapping({ "/admin/template" })
public class TemplateController extends BaseController {

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Resource(name = "templateServiceImpl")
	private TemplateService templateService;

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(String id, ModelMap model) {
		if (StringUtils.isEmpty(id)){
			return "/admin/common/error";
		}
		
		model.addAttribute("template", this.templateService.get(id));
		model.addAttribute("content", this.templateService.read(id));
		
		return "/admin/template/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(String id, String content,
			RedirectAttributes redirectAttributes) {
		if ((StringUtils.isEmpty(id)) || (content == null)){
			return "/admin/common/error";
		}
		
		this.templateService.write(id, content);
		this.freeMarkerConfigurer.getConfiguration().clearTemplateCache();
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(TemplateType type, ModelMap model) {
		model.addAttribute("type", type);
		model.addAttribute("types", TemplateType.values());
		model.addAttribute("templates", this.templateService.getList(type));
		
		return "/admin/template/list";
	}
}