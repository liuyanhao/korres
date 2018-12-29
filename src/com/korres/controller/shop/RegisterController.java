package com.korres.controller.shop;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
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

import com.korres.entity.Area;
import com.korres.entity.BaseEntity.BaseEntitySave;
import com.korres.entity.Cart;
import com.korres.entity.Member;
import com.korres.entity.Member.MemberGender;
import com.korres.entity.MemberAttribute;
import com.korres.entity.MemberAttribute.MemberAttributeType;
import com.korres.service.AreaService;
import com.korres.service.CaptchaService;
import com.korres.service.CartService;
import com.korres.service.MemberAttributeService;
import com.korres.service.MemberRankService;
import com.korres.service.MemberService;
import com.korres.service.RSAService;
import com.korres.util.CookieUtils;
import com.korres.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.CommonAttributes;
import com.korres.Message;
import com.korres.Principal;
import com.korres.Setting;

@Controller("shopRegisterController")
@RequestMapping({ "/register" })
public class RegisterController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@RequestMapping(value = { "/check_username" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}

		return (!this.memberService.usernameDisabled(username))
				&& (!this.memberService.usernameExists(username));
	}

	@RequestMapping(value = { "/check_email" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}

		return !this.memberService.emailExists(email);
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		model.addAttribute("genders", MemberGender.values());
		model.addAttribute("captchaId", UUID.randomUUID().toString());

		return "/shop/register/index";
	}

	@RequestMapping(value = { "/submit" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message submit(String captchaId, String captcha, String username,
			String email, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		String str = this.rsaService.decryptParameter("enPassword", request);
		this.rsaService.removePrivateKey(request);
		if (!this.captchaService.isValid(Setting.CaptchaType.memberRegister,
				captchaId, captcha)) {
			return Message.error("shop.captcha.invalid", new Object[0]);
		}

		Setting setting = SettingUtils.get();
		if (!setting.getIsRegisterEnabled().booleanValue()) {
			return Message.error("shop.register.disabled", new Object[0]);
		}

		if (validate(Member.class, "username", username,
				new Class[] { BaseEntitySave.class })) {
			if (validate(Member.class, "password", str,
					new Class[] { BaseEntitySave.class })) {
				if (!validate(Member.class, "email", email,
						new Class[] { BaseEntitySave.class })) {
					return Message.error("shop.common.invalid", new Object[0]);
				}
			}
			else{
				return Message.error("shop.common.invalid", new Object[0]);
			}
		}
		else {
			return Message.error("shop.common.invalid", new Object[0]);
		}

		if ((username.length() < setting.getUsernameMinLength().intValue())
				|| (username.length() > setting.getUsernameMaxLength()
						.intValue())) {
			return Message.error("shop.common.invalid", new Object[0]);
		}

		if ((str.length() < setting.getPasswordMinLength().intValue())
				|| (str.length() > setting.getPasswordMaxLength().intValue())) {
			return Message.error("shop.common.invalid", new Object[0]);
		}

		if ((this.memberService.usernameDisabled(username))
				|| (this.memberService.usernameExists(username))) {
			return Message.error("shop.register.disabledExist", new Object[0]);
		}

		if ((!setting.getIsDuplicateEmail().booleanValue())
				&& (this.memberService.emailExists(email))) {
			return Message.error("shop.register.emailExist", new Object[0]);
		}

		Member member = new Member();
		List<MemberAttribute> lima = this.memberAttributeService.findList();
		Iterator<MemberAttribute> iterator = lima.iterator();
		while (iterator.hasNext()) {
			MemberAttribute memberAttribute = iterator.next();
			String memberAttributeStr = request.getParameter("memberAttribute_"
					+ memberAttribute.getId());
			if ((memberAttribute.getType() == MemberAttributeType.name)
					|| (memberAttribute.getType() == MemberAttributeType.address)
					|| (memberAttribute.getType() == MemberAttributeType.zipCode)
					|| (memberAttribute.getType() == MemberAttributeType.phone)
					|| (memberAttribute.getType() == MemberAttributeType.mobile)
					|| (memberAttribute.getType() == MemberAttributeType.text)
					|| (memberAttribute.getType() == MemberAttributeType.select)) {
				if ((memberAttribute.getIsRequired().booleanValue())
						&& (StringUtils.isEmpty(memberAttributeStr))) {
					return Message.error("shop.common.invalid", new Object[0]);
				}

				member.setAttributeValue(memberAttribute, memberAttributeStr);
			} else {
				if (memberAttribute.getType() == MemberAttributeType.gender) {
					MemberGender gender = StringUtils
							.isNotEmpty(memberAttributeStr) ? MemberGender
							.valueOf(memberAttributeStr) : null;
					if ((memberAttribute.getIsRequired().booleanValue())
							&& (gender == null)) {
						return Message.error("shop.common.invalid",
								new Object[0]);
					}

					member.setGender(gender);
				} else if (memberAttribute.getType() == MemberAttributeType.birth) {
					try {
						Date birth = StringUtils.isNotEmpty(memberAttributeStr) ? DateUtils
								.parseDate(memberAttributeStr,
										CommonAttributes.DATE_PATTERNS) : null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& (birth == null)) {
							return Message.error("shop.common.invalid",
									new Object[0]);
						}

						member.setBirth(birth);
					} catch (ParseException e) {
						e.printStackTrace();
						return Message.error("shop.common.invalid",
								new Object[0]);
					}
				} else if (memberAttribute.getType() == MemberAttributeType.area) {
					Area area = StringUtils.isNotEmpty(memberAttributeStr) ? (Area) this.areaService
							.find(Long.valueOf(memberAttributeStr)) : null;
					if (area != null) {
						member.setArea(area);
					} else if (memberAttribute.getIsRequired().booleanValue()) {
						return Message.error("shop.common.invalid",
								new Object[0]);
					}
				} else if (memberAttribute.getType() == MemberAttributeType.checkbox) {
					String[] lim = request
							.getParameterValues("memberAttribute_"
									+ memberAttribute.getId());
					List<String> liStr = lim != null ? Arrays.asList(lim)
							: null;
					if ((memberAttribute.getIsRequired().booleanValue())
							&& (liStr == null) || (liStr.isEmpty())) {
						return Message.error("shop.common.invalid",
								new Object[0]);
					}

					member.setAttributeValue(memberAttribute,
							memberAttributeStr);
				}
			}
		}

		member.setUsername(username.toLowerCase());
		member.setPassword(DigestUtils.md5Hex(str));
		member.setEmail(email);
		member.setPoint(setting.getRegisterPoint());
		member.setAmount(new BigDecimal(0));
		member.setBalance(new BigDecimal(0));
		member.setIsEnabled(Boolean.valueOf(true));
		member.setIsLocked(Boolean.valueOf(false));
		member.setLoginFailureCount(Integer.valueOf(0));
		member.setLockedDate(null);
		member.setRegisterIp(request.getRemoteAddr());
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setSafeKey(null);
		member.setMemberRank(this.memberRankService.findDefault());
		member.setFavoriteProducts(null);
		this.memberService.save(member);

		Cart cart = this.cartService.getCurrent();
		if ((cart != null) && (cart.getMember() == null)) {
			this.cartService.merge(member, cart);
			CookieUtils.removeCookie(request, response, "cartId");
			CookieUtils.removeCookie(request, response, "cartKey");
		}

		Map map = new HashMap();
		Enumeration enumeration = session.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String element = (String) enumeration.nextElement();
			map.put(element, session.getAttribute(element));
		}

		session.invalidate();
		session = request.getSession();
		Iterator iterator2 = map.entrySet().iterator();
		while (iterator2.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator2.next();
			session.setAttribute((String) entry.getKey(), entry.getValue());
		}

		session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(
				member.getId(), member.getUsername()));
		CookieUtils.addCookie(request, response, "username",
				member.getUsername());

		return Message.success("shop.register.success", new Object[0]);
	}
}