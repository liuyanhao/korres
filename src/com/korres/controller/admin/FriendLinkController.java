package com.korres.controller.admin;

import javax.annotation.Resource;

import com.korres.entity.FriendLink;
import com.korres.entity.FriendLink.FriendLinkType;
import com.korres.service.FriendLinkService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminFriendLinkController")
@RequestMapping( { "/admin/friend_link" })
public class FriendLinkController extends BaseController {

	@Resource(name = "friendLinkServiceImpl")
	private FriendLinkService friendLinkService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("types", FriendLinkType.values());
		return "/admin/friend_link/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(FriendLink friendLink,
			RedirectAttributes redirectAttributes) {
		if (!validator(friendLink, new Class[0])){
			return "/admin/common/error";
		}
		
		if (friendLink.getType() == FriendLinkType.text){
			friendLink.setLogo(null);
		}
		else if (StringUtils.isEmpty(friendLink.getLogo())){
			return "/admin/common/error";
		}
		
		this.friendLinkService.save(friendLink);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", FriendLinkType.values());
		model.addAttribute("friendLink", this.friendLinkService.find(id));
		
		return "/admin/friend_link/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(FriendLink friendLink,
			RedirectAttributes redirectAttributes) {
		if (!validator(friendLink, new Class[0])) {
			return "/admin/common/error";
		}

		if (friendLink.getType() == FriendLinkType.text) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			return "/admin/common/error";
		}

		this.friendLinkService.update(friendLink);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.friendLinkService.findPage(pageable));

		return "/admin/friend_link/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.friendLinkService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}