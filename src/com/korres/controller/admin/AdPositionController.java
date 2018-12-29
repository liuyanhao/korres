package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.AdPosition;
import com.korres.service.AdPositionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminAdPositionController")
@RequestMapping({ "/admin/ad_position" })
public class AdPositionController extends BaseController {

	@Resource(name = "adPositionServiceImpl")
	private AdPositionService adPositionService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/ad_position/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(AdPosition adPosition,
			RedirectAttributes redirectAttributes) {
		if (!validator(adPosition, new Class[0])) {
			return "/admin/common/error";
		}

		adPosition.setAds(null);
		this.adPositionService.save(adPosition);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("adPosition", this.adPositionService.find(id));
		return "/admin/ad_position/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(AdPosition adPosition,
			RedirectAttributes redirectAttributes) {
		if (!validator(adPosition, new Class[0])) {
			return "/admin/common/error";
		}

		this.adPositionService.update(adPosition, new String[] { "ads" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.adPositionService.findPage(pageable));
		return "/admin/ad_position/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.adPositionService.delete(ids);
		
		return ADMIN_MESSAGE_SUCCESS;
	}
}