package com.korres.controller.shop;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.korres.entity.Cart;
import com.korres.entity.Member;
import com.korres.service.CaptchaService;
import com.korres.service.CartService;
import com.korres.service.MemberService;
import com.korres.service.RSAService;
import com.korres.util.CookieUtils;
import com.korres.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Principal;
import com.korres.Setting;

@Controller("shopLoginController")
@RequestMapping({ "/login" })
public class LoginController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@RequestMapping(value = { "/check" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Boolean check() {
		return Boolean.valueOf(this.memberService.isAuthenticated());
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(String redirectUrl, HttpServletRequest request,
			ModelMap model) {
		Setting setting = SettingUtils.get();
		if ((redirectUrl != null)
				&& (!redirectUrl.equalsIgnoreCase(setting.getSiteUrl()))
				&& (!redirectUrl.startsWith(request.getContextPath() + "/"))
				&& (!redirectUrl.startsWith(setting.getSiteUrl() + "/"))) {
			redirectUrl = null;
		}

		model.addAttribute("redirectUrl", redirectUrl);
		model.addAttribute("captchaId", UUID.randomUUID().toString());

		return "/shop/login/index";
	}

	@RequestMapping(value = { "/submit" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message submit(String captchaId, String captcha, String username,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		String str = this.rsaService.decryptParameter("enPassword", request);
		this.rsaService.removePrivateKey(request);
		if (!this.captchaService.isValid(Setting.CaptchaType.memberLogin,
				captchaId, captcha)) {
			return Message.error("shop.captcha.invalid", new Object[0]);
		}

		if ((StringUtils.isEmpty(username)) || (StringUtils.isEmpty(str))) {
			return Message.error("shop.common.invalid", new Object[0]);
		}

		Setting setting = SettingUtils.get();
		Member member = null;
		if ((setting.getIsEmailLogin().booleanValue())
				&& (username.contains("@"))) {
			List<Member> lim = this.memberService.findListByEmail(username);
			if (lim.isEmpty()) {
				member = null;
			} else if (lim.size() == 1) {
				member = lim.get(0);
			} else {
				return Message.error("shop.login.unsupportedAccount",
						new Object[0]);
			}
		} else {
			member = this.memberService.findByUsername(username);
		}

		if (member == null) {
			return Message.error("shop.login.unknownAccount", new Object[0]);
		}

		if (!member.getIsEnabled().booleanValue()) {
			return Message.error("shop.login.disabledAccount", new Object[0]);
		}

		int accountLockTime = 0;
		if (member.getIsLocked().booleanValue())
			if (ArrayUtils.contains(setting.getAccountLockTypes(),
					Setting.AccountLockType.member)) {
				accountLockTime = setting.getAccountLockTime().intValue();
				if (accountLockTime == 0) {
					return Message.error("shop.login.lockedAccount",
							new Object[0]);
				}

				Date lockedDate = member.getLockedDate();
				Date lockedTime = DateUtils.addMinutes(lockedDate,
						accountLockTime);
				if (new Date().after(lockedTime)) {
					member.setLoginFailureCount(Integer.valueOf(0));
					member.setIsLocked(Boolean.valueOf(false));
					member.setLockedDate(null);
					this.memberService.update(member);
				} else {
					return Message.error("shop.login.lockedAccount",
							new Object[0]);
				}
			} else {
				member.setLoginFailureCount(Integer.valueOf(0));
				member.setIsLocked(Boolean.valueOf(false));
				member.setLockedDate(null);
				this.memberService.update(member);
			}
		if (!DigestUtils.md5Hex(str).equals(member.getPassword())) {
			accountLockTime = member.getLoginFailureCount().intValue() + 1;
			if (accountLockTime >= setting.getAccountLockCount().intValue()) {
				member.setIsLocked(Boolean.valueOf(true));
				member.setLockedDate(new Date());
			}

			member.setLoginFailureCount(Integer.valueOf(accountLockTime));
			this.memberService.update(member);
			if (ArrayUtils.contains(setting.getAccountLockTypes(),
					Setting.AccountLockType.member)) {
				return Message.error("shop.login.accountLockCount",
						new Object[] { setting.getAccountLockCount() });
			}

			return Message.error("shop.login.incorrectCredentials",
					new Object[0]);
		}

		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginFailureCount(Integer.valueOf(0));
		this.memberService.update(member);
		Cart cart = this.cartService.getCurrent();
		if ((cart != null) && (cart.getMember() == null)) {
			this.cartService.merge(member, cart);
			CookieUtils.removeCookie(request, response, "cartId");
			CookieUtils.removeCookie(request, response, "cartKey");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enumeration = session.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String element = (String) enumeration.nextElement();
			map.put(element, session.getAttribute(element));
		}

		session.invalidate();
		session = request.getSession();
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			session.setAttribute((String) entry.getKey(), entry.getValue());
		}

		session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(
				member.getId(), username));
		CookieUtils.addCookie(request, response, "username",
				member.getUsername());

		return SHOP_MESSAGE_SUCCESS;
	}
}