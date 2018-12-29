package com.korres.plugin.ftp;

import javax.annotation.Resource;
import com.korres.controller.admin.BaseController;
import com.korres.entity.PluginConfig;
import com.korres.service.PluginConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminPluginFtpController")
@RequestMapping( { "/admin/storage_plugin/ftp" })
public class FtpController extends BaseController {

	@Resource(name = "ftpPlugin")
	private FtpPlugin ftpPlugin;

	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	@RequestMapping(value = { "/install" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String install(RedirectAttributes redirectAttributes) {
		if (!this.ftpPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(this.ftpPlugin.getId());
			pluginConfig.setIsEnabled(Boolean.valueOf(false));

			this.pluginConfigService.save(pluginConfig);
		}

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}

	@RequestMapping(value = { "/uninstall" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String uninstall(RedirectAttributes redirectAttributes) {
		if (this.ftpPlugin.getIsInstalled()) {
			PluginConfig localPluginConfig = this.ftpPlugin.getPluginConfig();
			this.pluginConfigService.delete(localPluginConfig);
		}

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}

	@RequestMapping(value = { "/setting" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String setting(ModelMap model) {
		PluginConfig localPluginConfig = this.ftpPlugin.getPluginConfig();
		model.addAttribute("pluginConfig", localPluginConfig);

		return "/com/korres/plugin/ftp/setting";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(String host, Integer port, String username,
			String password, String urlPrefix,
			@RequestParam(defaultValue = "false") Boolean isEnabled,
			Integer order, RedirectAttributes redirectAttributes) {
		PluginConfig pluginConfig = this.ftpPlugin.getPluginConfig();
		pluginConfig.setAttribute("host", host);
		pluginConfig.setAttribute("port", port.toString());
		pluginConfig.setAttribute("username", username);
		pluginConfig.setAttribute("password", password);
		pluginConfig.setAttribute("urlPrefix", StringUtils.removeEnd(urlPrefix,
				"/"));
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrder(order);
		this.pluginConfigService.update(pluginConfig);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}
}