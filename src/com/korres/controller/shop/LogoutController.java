package com.korres.controller.shop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.korres.entity.Member;
import com.korres.util.CookieUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("shopLogoutController")
public class LogoutController extends BaseController {
	@RequestMapping(value = { "/logout" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String execute(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		session.removeAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
		CookieUtils.removeCookie(request, response, "username");

		return "redirect:/";
	}
}