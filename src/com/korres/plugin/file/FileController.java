package com.korres.plugin.file;

import javax.annotation.Resource;
import com.korres.controller.admin.BaseController;
import com.korres.entity.PluginConfig;
import com.korres.service.PluginConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminPluginFileController")
@RequestMapping( { "/admin/storage_plugin/file" })
public class FileController extends BaseController {

	@Resource(name = "filePlugin")
	private FilePlugin filePlugin;

	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	@RequestMapping(value = { "/setting" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String setting(ModelMap model) {
		PluginConfig pluginConfig = this.filePlugin.getPluginConfig();
		model.addAttribute("pluginConfig", pluginConfig);

		return "/com/korres/plugin/file/setting";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Integer order, RedirectAttributes redirectAttributes) {
		PluginConfig pluginConfig = this.filePlugin.getPluginConfig();
		pluginConfig.setIsEnabled(Boolean.valueOf(true));
		pluginConfig.setOrder(order);
		this.pluginConfigService.update(pluginConfig);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}
}