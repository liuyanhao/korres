package com.korres.plugin.oss;

import java.math.BigDecimal;
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

import com.korres.Message;

@Controller("adminPluginOssController")
@RequestMapping( { "/admin/storage_plugin/oss" })
public class OssController extends BaseController {

	@Resource(name = "ossPlugin")
	private OssPlugin ossPlugin;

	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	@RequestMapping(value = { "/install" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String install(RedirectAttributes redirectAttributes) {
		String str = System.getProperty("java.specification.version");
		if (StringUtils.isNotEmpty(str)) {
			BigDecimal version = new BigDecimal(str);
			if (version.compareTo(new BigDecimal("1.6")) < 0) {
				setRedirectAttributes(redirectAttributes, Message.error(
						"admin.plugin.oss.unsupportedJavaVersion",
						new Object[0]));
				return "redirect:/admin/storage_plugin/list.jhtml";
			}
		}
		if (!this.ossPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(this.ossPlugin.getId());
			pluginConfig.setIsEnabled(Boolean.valueOf(false));
			this.pluginConfigService.save(pluginConfig);
		}

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}

	@RequestMapping(value = { "/uninstall" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String uninstall(RedirectAttributes redirectAttributes) {
		if (this.ossPlugin.getIsInstalled()) {
			PluginConfig localPluginConfig = this.ossPlugin.getPluginConfig();
			this.pluginConfigService.delete(localPluginConfig);
		}

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}

	@RequestMapping(value = { "/setting" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String setting(ModelMap model) {
		PluginConfig pluginConfig = this.ossPlugin.getPluginConfig();
		model.addAttribute("pluginConfig", pluginConfig);
		return "/com/korres/plugin/oss/setting";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(String accessId, String accessKey, String bucketName,
			String urlPrefix,
			@RequestParam(defaultValue = "false") Boolean isEnabled,
			Integer order, RedirectAttributes redirectAttributes) {
		PluginConfig pluginConfig = this.ossPlugin.getPluginConfig();
		pluginConfig.setAttribute("accessId", accessId);
		pluginConfig.setAttribute("accessKey", accessKey);
		pluginConfig.setAttribute("bucketName", bucketName);
		pluginConfig.setAttribute("urlPrefix", StringUtils.removeEnd(urlPrefix,
				"/"));
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrder(order);
		this.pluginConfigService.update(pluginConfig);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/storage_plugin/list.jhtml";
	}
}