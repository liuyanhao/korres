package com.korres.controller.admin;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.korres.entity.MemberRank;
import com.korres.service.MemberRankService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminMemberRankController")
@RequestMapping( { "/admin/member_rank" })
public class MemberRankController extends BaseController {

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@RequestMapping(value = { "/check_name" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkName(String previousName, String name) {
		if (StringUtils.isEmpty(name))
			return false;
		return this.memberRankService.nameUnique(previousName, name);
	}

	@RequestMapping(value = { "/check_amount" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkAmount(BigDecimal previousAmount, BigDecimal amount) {
		if (amount == null) {
			return false;
		}

		return this.memberRankService.amountUnique(previousAmount, amount);
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/member_rank/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(MemberRank memberRank,
			RedirectAttributes redirectAttributes) {
		if (!validator(memberRank, new Class[0])) {
			return "/admin/common/error";
		}

		if (this.memberRankService.nameExists(memberRank.getName())) {
			return "/admin/common/error";
		}

		if (memberRank.getIsSpecial().booleanValue()) {
			memberRank.setAmount(null);
		} else if ((memberRank.getAmount() == null)
				|| (this.memberRankService.amountExists(memberRank.getAmount()))) {
			return "/admin/common/error";
		}

		memberRank.setMembers(null);
		memberRank.setPromotions(null);
		this.memberRankService.save(memberRank);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("memberRank", this.memberRankService.find(id));

		return "/admin/member_rank/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(MemberRank memberRank,
			RedirectAttributes redirectAttributes) {
		if (!validator(memberRank, new Class[0])) {
			return "/admin/common/error";
		}

		MemberRank localMemberRank = (MemberRank) this.memberRankService
				.find(memberRank.getId());
		if (localMemberRank == null) {
			return "/admin/common/error";
		}

		if (!this.memberRankService.nameUnique(localMemberRank.getName(),
				memberRank.getName())) {
			return "/admin/common/error";
		}

		if (localMemberRank.getIsDefault().booleanValue()) {
			memberRank.setIsDefault(Boolean.valueOf(true));
		}

		if (memberRank.getIsSpecial().booleanValue()) {
			memberRank.setAmount(null);
		} else if ((memberRank.getAmount() == null)
				|| (!this.memberRankService.amountUnique(localMemberRank
						.getAmount(), memberRank.getAmount()))) {
			return "/admin/common/error";
		}

		this.memberRankService.update(memberRank, new String[] { "members",
				"promotions" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.memberRankService.findPage(pageable));

		return "/admin/member_rank/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				MemberRank localMemberRank = (MemberRank) this.memberRankService
						.find(id);
				if ((localMemberRank != null)
						&& (localMemberRank.getMembers() != null)
						&& (!localMemberRank.getMembers().isEmpty())) {
					return Message.error(
							"admin.memberRank.deleteExistNotAllowed",
							new Object[] { localMemberRank.getName() });
				}
			}

			long count = this.memberRankService.count();
			if (ids.length >= count)
				return Message.error("admin.common.deleteAllNotAllowed",
						new Object[0]);
			this.memberRankService.delete(ids);
		}

		return ADMIN_MESSAGE_SUCCESS;
	}
}