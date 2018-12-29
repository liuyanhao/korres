package com.korres.controller.shop;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import com.korres.entity.Member;
import com.korres.entity.SafeKey;
import com.korres.entity.BaseEntity.BaseEntitySave;
import com.korres.service.CaptchaService;
import com.korres.service.MailService;
import com.korres.service.MemberService;
import com.korres.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Setting;

@Controller("shopPasswordController")
@RequestMapping({ "/password" })
public class PasswordController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "mailServiceImpl")
	private MailService mailService;

	@RequestMapping(value = { "/find" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String find(Model model) {
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		return "/shop/password/find";
	}

	@RequestMapping(value = { "/find" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message find(String captchaId, String captcha, String username,
			String email) {
		if (!this.captchaService.isValid(Setting.CaptchaType.findPassword,
				captchaId, captcha)) {
			return Message.error("shop.captcha.invalid", new Object[0]);
		}

		if ((StringUtils.isEmpty(username)) || (StringUtils.isEmpty(email))) {
			return Message.error("shop.common.invalid", new Object[0]);
		}

		Member member = this.memberService.findByUsername(username);
		if (member == null) {
			return Message.error("shop.password.memberNotExist", new Object[0]);
		}

		if (!member.getEmail().equalsIgnoreCase(email)) {
			return Message.error("shop.password.invalidEmail", new Object[0]);
		}

		Setting setting = SettingUtils.get();
		SafeKey safeKey = new SafeKey();
		safeKey.setValue(UUID.randomUUID().toString()
				+ DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
		safeKey.setExpire(setting.getSafeKeyExpiryTime().intValue() != 0 ? DateUtils
				.addMinutes(new Date(), setting.getSafeKeyExpiryTime()
						.intValue()) : null);
		member.setSafeKey(safeKey);
		this.memberService.update(member);
		this.mailService.sendFindPasswordMail(member.getEmail(),
				member.getUsername(), safeKey);

		return Message.success("shop.password.mailSuccess", new Object[0]);
	}

	@RequestMapping(value = { "/reset" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String reset(String username, String key, Model model) {
		Member member = this.memberService.findByUsername(username);
		if (member == null) {
			return SHOP_COMMON_ERROR;
		}

		SafeKey safeKey = member.getSafeKey();
		if ((safeKey == null) || (safeKey.getValue() == null)
				|| (!safeKey.getValue().equals(key))) {
			return SHOP_COMMON_ERROR;
		}

		if (safeKey.hasExpired()) {
			model.addAttribute("erroInfo",
					Message.warn("shop.password.hasExpired", new Object[0]));
			return SHOP_COMMON_ERROR;
		}

		model.addAttribute("captchaId", UUID.randomUUID().toString());
		model.addAttribute("member", member);
		model.addAttribute("key", key);

		return "/shop/password/reset";
	}

	@RequestMapping(value = { "reset" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message reset(String captchaId, String captcha, String username,
			String newPassword, String key) {
		if (!this.captchaService.isValid(Setting.CaptchaType.resetPassword,
				captchaId, captcha)) {
			return Message.error("shop.captcha.invalid", new Object[0]);
		}

		Member member = this.memberService.findByUsername(username);
		if (member == null) {
			return SHOP_MESSAGE_ERROR;
		}

		if (!validate(Member.class, "password", newPassword,
				new Class[] { BaseEntitySave.class })) {
			return Message.warn("shop.password.invalidPassword", new Object[0]);
		}

		Setting setting = SettingUtils.get();
		if ((newPassword.length() < setting.getPasswordMinLength().intValue())
				|| (newPassword.length() > setting.getPasswordMaxLength()
						.intValue())) {
			return Message.warn("shop.password.invalidPassword", new Object[0]);
		}

		SafeKey safeKey = member.getSafeKey();
		if ((safeKey == null) || (safeKey.getValue() == null)
				|| (!safeKey.getValue().equals(key))) {
			return SHOP_MESSAGE_ERROR;
		}

		if (safeKey.hasExpired()) {
			return Message.error("shop.password.hasExpired", new Object[0]);
		}

		member.setPassword(DigestUtils.md5Hex(newPassword));
		safeKey.setExpire(new Date());
		safeKey.setValue(null);
		this.memberService.update(member);

		return Message.success("shop.password.resetSuccess", new Object[0]);
	}
}