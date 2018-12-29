package com.korres.controller.shop.member;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.controller.shop.BaseController;
import com.korres.entity.Area;
import com.korres.entity.Member;
import com.korres.entity.Member.MemberGender;
import com.korres.entity.MemberAttribute;
import com.korres.entity.MemberAttribute.MemberAttributeType;
import com.korres.service.AreaService;
import com.korres.service.MemberAttributeService;
import com.korres.service.MemberService;
import com.korres.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.CommonAttributes;
import com.korres.Setting;

@Controller("shopMemberProfileController")
@RequestMapping( { "/member/profile" })
public class ProfileController extends BaseController {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@RequestMapping(value = { "/check_email" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}

		Member member = this.memberService.getCurrent();

		return this.memberService.emailUnique(member.getEmail(), email);
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(ModelMap model) {
		model.addAttribute("genders", MemberGender.values());
		model.addAttribute("memberAttributes", this.memberAttributeService
				.findList());

		return "shop/member/profile/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(String email, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (!validate(Member.class, "email", email, new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		Setting setting = SettingUtils.get();
		Member member = this.memberService.getCurrent();
		if ((!setting.getIsDuplicateEmail().booleanValue())
				&& (!this.memberService.emailUnique(member.getEmail(), email))) {
			return SHOP_COMMON_ERROR;
		}

		member.setEmail(email);
		List<MemberAttribute> lima = this.memberAttributeService.findList();
		Iterator<MemberAttribute> iterator = lima.iterator();
		while (iterator.hasNext()) {
			MemberAttribute memberAttribute = iterator.next();
			String str = request.getParameter("memberAttribute_"
					+ memberAttribute.getId());
			if ((memberAttribute.getType() == MemberAttributeType.name)
					|| (memberAttribute.getType() == MemberAttributeType.address)
					|| (memberAttribute.getType() == MemberAttributeType.zipCode)
					|| (memberAttribute.getType() == MemberAttributeType.phone)
					|| (memberAttribute.getType() == MemberAttributeType.mobile)
					|| (memberAttribute.getType() == MemberAttributeType.text)
					|| (memberAttribute.getType() == MemberAttributeType.select)) {
				if ((memberAttribute.getIsRequired().booleanValue())
						&& (StringUtils.isEmpty(str))) {
					return SHOP_COMMON_ERROR;
				}

				member.setAttributeValue(memberAttribute, str);
			} else {
				if (memberAttribute.getType() == MemberAttributeType.gender) {
					MemberGender gender = StringUtils.isNotEmpty(str) ? MemberGender
							.valueOf(str)
							: null;
					if ((memberAttribute.getIsRequired().booleanValue())
							&& (gender == null)) {
						return SHOP_COMMON_ERROR;
					}

					member.setGender(gender);
				} else if (memberAttribute.getType() == MemberAttributeType.birth) {
					try {
						Date birth = StringUtils.isNotEmpty(str) ? DateUtils
								.parseDate(str, CommonAttributes.DATE_PATTERNS)
								: null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& (birth == null)) {
							return SHOP_COMMON_ERROR;
						}

						member.setBirth(birth);
					} catch (ParseException e) {
						e.printStackTrace();
						return SHOP_COMMON_ERROR;
					}
				} else {
					if (memberAttribute.getType() == MemberAttributeType.area) {
						Area area = StringUtils.isNotEmpty(str) ? this.areaService
								.find(Long.valueOf(str))
								: null;
						if (area != null) {
							member.setArea(area);
						} else if (memberAttribute.getIsRequired()
								.booleanValue()) {
							return SHOP_COMMON_ERROR;
						}
					} else if (memberAttribute.getType() == MemberAttributeType.checkbox) {
						String[] memberAttributeValues = request
								.getParameterValues("memberAttribute_"
										+ memberAttribute.getId());
						List<String> list = memberAttributeValues != null ? Arrays
								.asList(memberAttributeValues)
								: null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& ((list == null) || (list.isEmpty()))) {
							return SHOP_COMMON_ERROR;
						}

						member.setAttributeValue(memberAttribute, list);
					}
				}
			}
		}

		this.memberService.update(member);
		setRedirect(redirectAttributes, SHOP_MESSAGE_SUCCESS);

		return "redirect:edit.jhtml";
	}
}