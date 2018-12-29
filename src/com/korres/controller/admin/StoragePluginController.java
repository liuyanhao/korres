package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.service.PluginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminStoragePluginController")
@RequestMapping({ "/admin/storage_plugin" })
public class StoragePluginController extends BaseController {

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ModelMap model) {
		model.addAttribute("storagePlugins",
				this.pluginService.getStoragePlugins());
		return "/admin/storage_plugin/list";
	}
}