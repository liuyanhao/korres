package com.korres.controller.shop.member;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.service.MemberService;
import com.korres.util.SettingUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Setting;

@Controller("shopMemberPasswordController")
@RequestMapping( { "/member/password" })
public class PasswordController extends BaseController {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@RequestMapping(value = { "/check_current_password" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkCurrentPassword(String currentPassword) {
		if (StringUtils.isEmpty(currentPassword)) {
			return false;
		}

		Member member = this.memberService.getCurrent();

		return StringUtils.equals(DigestUtils.md5Hex(currentPassword), member
				.getPassword());
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit() {
		return "shop/member/password/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(String currentPassword, String password,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if ((StringUtils.isEmpty(password))
				|| (StringUtils.isEmpty(currentPassword))) {
			return SHOP_COMMON_ERROR;
		}

		if (!validate(Member.class, "password", password, new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		Setting setting = SettingUtils.get();
		if ((password.length() < setting.getPasswordMinLength().intValue())
				|| (password.length() > setting.getPasswordMaxLength()
						.intValue())) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (!StringUtils.equals(DigestUtils.md5Hex(currentPassword), member
				.getPassword())) {
			return SHOP_COMMON_ERROR;
		}

		member.setPassword(DigestUtils.md5Hex(password));
		this.memberService.update(member);
		setRedirect(redirectAttributes, SHOP_MESSAGE_SUCCESS);

		return "redirect:edit.jhtml";
	}
}