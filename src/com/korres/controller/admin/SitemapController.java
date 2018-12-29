package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.service.StaticService;
import com.korres.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Template;

@Controller("adminSitemapController")
@RequestMapping({ "/admin/sitemap" })
public class SitemapController extends BaseController {

	@Resource(name = "templateServiceImpl")
	private TemplateService templateService;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@RequestMapping(value = { "/build" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String build(ModelMap model) {
		Template template = this.templateService.get("sitemapIndex");
		model.addAttribute("sitemapIndexPath", template.getStaticPath());
		
		return "/admin/sitemap/build";
	}

	@RequestMapping(value = { "/build" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String build(RedirectAttributes redirectAttributes) {
		this.staticService.buildSitemap();
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:build.jhtml";
	}
}