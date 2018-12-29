package com.korres.controller.admin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.korres.entity.Area;
import com.korres.entity.BaseEntity.BaseEntitySave;
import com.korres.entity.Member;
import com.korres.entity.Member.MemberGender;
import com.korres.entity.MemberAttribute;
import com.korres.entity.MemberAttribute.MemberAttributeType;
import com.korres.entity.MemberRank;
import com.korres.service.AdminService;
import com.korres.service.AreaService;
import com.korres.service.MemberAttributeService;
import com.korres.service.MemberRankService;
import com.korres.service.MemberService;
import com.korres.util.SettingUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.CommonAttributes;
import com.korres.Message;
import com.korres.Pageable;
import com.korres.Setting;

@Controller("adminMemberController")
@RequestMapping( { "/admin/member" })
public class MemberController extends BaseController {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/check_username" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkUsername(String username) {
		if (StringUtils.isEmpty(username))
			return false;
		return (!this.memberService.usernameDisabled(username))
				&& (!this.memberService.usernameExists(username));
	}

	@RequestMapping(value = { "/check_email" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkEmail(String previousEmail, String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}

		return this.memberService.emailUnique(previousEmail, email);
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("genders", MemberGender.values());
		model.addAttribute("memberAttributes", this.memberAttributeService
				.findList());
		model.addAttribute("member", this.memberService.find(id));

		return "/admin/member/view";
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("genders", MemberGender.values());
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("memberAttributes", this.memberAttributeService
				.findList());

		return "/admin/member/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Member member, Long memberRankId,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		member.setMemberRank(this.memberRankService.find(memberRankId));
		if (!validator(member, new Class[] { BaseEntitySave.class })) {
			return "/admin/common/error";
		}

		Setting setting = SettingUtils.get();
		if ((member.getUsername().length() < setting.getUsernameMinLength()
				.intValue())
				|| (member.getUsername().length() > setting
						.getUsernameMaxLength().intValue())) {
			return "/admin/common/error";
		}

		if ((member.getPassword().length() < setting.getPasswordMinLength()
				.intValue())
				|| (member.getPassword().length() > setting
						.getPasswordMaxLength().intValue())) {
			return "/admin/common/error";
		}

		if ((this.memberService.usernameDisabled(member.getUsername()))
				|| (this.memberService.usernameExists(member.getUsername()))) {
			return "/admin/common/error";
		}

		if ((!setting.getIsDuplicateEmail().booleanValue())
				&& (this.memberService.emailExists(member.getEmail()))) {
			return "/admin/common/error";
		}

		member.removeAttributeValue();
		Iterator<MemberAttribute> iterator = this.memberAttributeService
				.findList().iterator();
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
					return "/admin/common/error";
				}

				member.setAttributeValue(memberAttribute, str);
			} else {
				if (memberAttribute.getType() == MemberAttributeType.gender) {
					MemberGender gender = StringUtils.isNotEmpty(str) ? MemberGender
							.valueOf(str)
							: null;
					if ((memberAttribute.getIsRequired().booleanValue())
							&& (gender == null)) {
						return "/admin/common/error";
					}

					member.setGender(gender);
				} else if (memberAttribute.getType() == MemberAttributeType.birth) {
					try {
						Date birth = StringUtils.isNotEmpty(str) ? DateUtils
								.parseDate(str, CommonAttributes.DATE_PATTERNS)
								: null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& (birth == null))
							return "/admin/common/error";
						member.setBirth(birth);
					} catch (ParseException e) {
						e.printStackTrace();
						return "/admin/common/error";
					}
				} else {
					if (memberAttribute.getType() == MemberAttributeType.area) {
						Area area = StringUtils.isNotEmpty(str) ? (Area) this.areaService
								.find(Long.valueOf(str))
								: null;
						if (area != null) {
							member.setArea(area);
						} else if (memberAttribute.getIsRequired()
								.booleanValue()) {
							return "/admin/common/error";
						}
					} else if (memberAttribute.getType() == MemberAttributeType.checkbox) {
						String[] lima = request
								.getParameterValues("memberAttribute_"
										+ memberAttribute.getId());
						List<String> ma = lima != null ? Arrays.asList(lima)
								: null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& ((ma == null) || (ma.isEmpty()))) {
							return "/admin/common/error";
						}

						member.setAttributeValue(memberAttribute, ma);
					}
				}
			}
		}

		member.setUsername(member.getUsername().toLowerCase());
		member.setPassword(DigestUtils.md5Hex(member.getPassword()));
		member.setAmount(new BigDecimal(0));
		member.setIsLocked(Boolean.valueOf(false));
		member.setLoginFailureCount(Integer.valueOf(0));
		member.setLockedDate(null);
		member.setRegisterIp(request.getRemoteAddr());
		member.setLoginIp(null);
		member.setLoginDate(null);
		member.setSafeKey(null);
		member.setCart(null);
		member.setOrders(null);
		member.setDeposits(null);
		member.setPayments(null);
		member.setCouponCodes(null);
		member.setReceivers(null);
		member.setReviews(null);
		member.setConsultations(null);
		member.setFavoriteProducts(null);
		member.setProductNotifies(null);
		member.setInMessages(null);
		member.setOutMessages(null);
		this.memberService.save(member, this.adminService.getCurrent());
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("genders", MemberGender.values());
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("memberAttributes", this.memberAttributeService
				.findList());
		model.addAttribute("member", this.memberService.find(id));

		return "/admin/member/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Member member, Long memberRankId, Integer modifyPoint,
			BigDecimal modifyBalance, String depositMemo,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		member.setMemberRank((MemberRank) this.memberRankService
				.find(memberRankId));
		if (!validator(member, new Class[0])) {
			return "/admin/common/error";
		}

		Setting setting = SettingUtils.get();
		if ((member.getPassword() != null)
				&& ((member.getPassword().length() < setting
						.getPasswordMinLength().intValue()) || (member
						.getPassword().length() > setting
						.getPasswordMaxLength().intValue()))) {
			return "/admin/common/error";
		}

		Member localMember = (Member) this.memberService.find(member.getId());
		if (localMember == null) {
			return "/admin/common/error";
		}

		if ((!setting.getIsDuplicateEmail().booleanValue())
				&& (!this.memberService.emailUnique(localMember.getEmail(),
						member.getEmail()))) {
			return "/admin/common/error";
		}

		member.removeAttributeValue();
		Iterator<MemberAttribute> iterator = this.memberAttributeService
				.findList().iterator();
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
					return "/admin/common/error";
				}

				member.setAttributeValue(memberAttribute, str);
			} else {
				if (memberAttribute.getType() == MemberAttributeType.gender) {
					MemberGender gender = StringUtils.isNotEmpty(str) ? MemberGender
							.valueOf(str)
							: null;
					if ((memberAttribute.getIsRequired().booleanValue())
							&& (gender == null)) {
						return "/admin/common/error";
					}

					member.setGender(gender);
				} else if (memberAttribute.getType() == MemberAttributeType.birth) {
					try {
						Date birth = StringUtils.isNotEmpty(str) ? DateUtils
								.parseDate(str, CommonAttributes.DATE_PATTERNS)
								: null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& (birth == null)) {
							return "/admin/common/error";
						}

						member.setBirth(birth);
					} catch (ParseException e) {
						e.printStackTrace();
						return "/admin/common/error";
					}
				} else {
					if (memberAttribute.getType() == MemberAttributeType.area) {
						Area area = StringUtils.isNotEmpty(str) ? (Area) this.areaService
								.find(Long.valueOf(str))
								: null;
						if (area != null) {
							member.setArea(area);
						} else if (memberAttribute.getIsRequired()
								.booleanValue()) {
							return "/admin/common/error";
						}
					} else if (memberAttribute.getType() == MemberAttributeType.checkbox) {
						String[] ma = request
								.getParameterValues("memberAttribute_"
										+ memberAttribute.getId());
						List<String> lima = ma != null ? Arrays.asList(ma)
								: null;
						if ((memberAttribute.getIsRequired().booleanValue())
								&& ((lima == null) || (lima.isEmpty()))) {
							return "/admin/common/error";
						}

						member.setAttributeValue(memberAttribute, lima);
					}
				}
			}
		}
		if (StringUtils.isEmpty(member.getPassword()))
			member.setPassword(localMember.getPassword());
		else
			member.setPassword(DigestUtils.md5Hex(member.getPassword()));
		if ((localMember.getIsLocked().booleanValue())
				&& (!member.getIsLocked().booleanValue())) {
			member.setLoginFailureCount(Integer.valueOf(0));
			member.setLockedDate(null);
		} else {
			member.setIsLocked(localMember.getIsLocked());
			member.setLoginFailureCount(localMember.getLoginFailureCount());
			member.setLockedDate(localMember.getLockedDate());
		}

		BeanUtils.copyProperties(member, localMember, new String[] {
				"username", "point", "amount", "balance", "registerIp",
				"loginIp", "loginDate", "safeKey", "cart", "orders",
				"deposits", "payments", "couponCodes", "receivers", "reviews",
				"consultations", "favoriteProducts", "productNotifies",
				"inMessages", "outMessages" });
		this.memberService.update(localMember, modifyPoint, modifyBalance,
				depositMemo, this.adminService.getCurrent());
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("memberAttributes", this.memberAttributeService
				.findAll());
		model.addAttribute("page", this.memberService.findPage(pageable));

		return "/admin/member/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Member member = (Member) this.memberService.find(id);
				if ((member != null)
						&& (member.getBalance().compareTo(new BigDecimal(0)) > 0)) {
					return Message.error(
							"admin.member.deleteExistDepositNotAllowed",
							new Object[] { member.getUsername() });
				}
			}

			this.memberService.delete(ids);
		}

		return ADMIN_MESSAGE_SUCCESS;
	}
}