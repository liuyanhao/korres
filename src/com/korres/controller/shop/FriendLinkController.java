package com.korres.controller.shop;

import javax.annotation.Resource;

import com.korres.entity.FriendLink.FriendLinkType;
import com.korres.service.FriendLinkService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("shopFriendLinkController")
@RequestMapping({ "/friend_link" })
public class FriendLinkController extends BaseController {

	@Resource(name = "friendLinkServiceImpl")
	private FriendLinkService friendLinkService;

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(ModelMap model) {
		model.addAttribute("textFriendLinks",
				this.friendLinkService.findList(FriendLinkType.text));
		model.addAttribute("imageFriendLinks",
				this.friendLinkService.findList(FriendLinkType.image));

		return "/shop/friend_link/index";
	}
}