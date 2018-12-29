package com.korres.plugin.alipayDirect;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.korres.controller.admin.BaseController;
import com.korres.entity.PluginConfig;
import com.korres.plugin.PaymentPlugin.PaymentPluginFeeType;
import com.korres.service.PluginConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminAlipayDirectController")
@RequestMapping( { "/admin/payment_plugin/alipay_direct" })
public class AlipayDirectController extends BaseController {

	@Resource(name = "alipayDirectPlugin")
	private AlipayDirectPlugin alipayDirectPlugin;

	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	@RequestMapping(value = { "/install" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String install(RedirectAttributes redirectAttributes) {
		if (!this.alipayDirectPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(this.alipayDirectPlugin.getId());
			pluginConfig.setIsEnabled(Boolean.valueOf(false));
			
			this.pluginConfigService.save(pluginConfig);
		}

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:/admin/payment_plugin/list.jhtml";
	}

	@RequestMapping(value = { "/uninstall" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String uninstall(RedirectAttributes redirectAttributes) {
		if (this.alipayDirectPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = this.alipayDirectPlugin
					.getPluginConfig();
			this.pluginConfigService.delete(pluginConfig);
		}
		
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:/admin/payment_plugin/list.jhtml";
	}

	@RequestMapping(value = { "/setting" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String setting(ModelMap model) {
		PluginConfig pluginConfig = this.alipayDirectPlugin
				.getPluginConfig();
		model.addAttribute("feeTypes", PaymentPluginFeeType.values());
		model.addAttribute("pluginConfig", pluginConfig);
		
		return "/com/korres/plugin/alipayDirect/setting";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(String paymentName, String sellerEmail, String partner, String key,
			PaymentPluginFeeType feeType, BigDecimal fee, String logo,
			String description,
			@RequestParam(defaultValue = "false") Boolean isEnabled,
			Integer order, RedirectAttributes redirectAttributes) {
		PluginConfig pluginConfig = this.alipayDirectPlugin
				.getPluginConfig();
		pluginConfig.setAttribute("paymentName", paymentName);
		pluginConfig.setAttribute("sellerEmail", sellerEmail);
		pluginConfig.setAttribute("partner", partner);
		pluginConfig.setAttribute("key", key);
		pluginConfig.setAttribute("feeType", feeType.toString());
		pluginConfig.setAttribute("fee", fee.toString());
		pluginConfig.setAttribute("logo", logo);
		pluginConfig.setAttribute("description", description);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrder(order);
		this.pluginConfigService.update(pluginConfig);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:/admin/payment_plugin/list.jhtml";
	}
}