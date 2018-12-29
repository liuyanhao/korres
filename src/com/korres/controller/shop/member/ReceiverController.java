package com.korres.controller.shop.member;

import javax.annotation.Resource;

import com.korres.controller.shop.BaseController;
import com.korres.entity.Area;
import com.korres.entity.Member;
import com.korres.entity.Receiver;
import com.korres.service.AreaService;
import com.korres.service.MemberService;
import com.korres.service.ReceiverService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("shopMemberReceiverController")
@RequestMapping( { "/member/receiver" })
public class ReceiverController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.receiverService.findPage(member,
				pageable));

		return "shop/member/receiver/list";
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(RedirectAttributes redirectAttributes) {
		Member member = this.memberService.getCurrent();
		if ((Receiver.MAX_RECEIVER_COUNT != null)
				&& (member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT
						.intValue())) {
			setRedirect(redirectAttributes, Message.warn(
					"shop.member.receiver.addCountNotAllowed",
					new Object[] { Receiver.MAX_RECEIVER_COUNT }));

			return "redirect:list.jhtml";
		}

		return "shop/member/receiver/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Receiver receiver, Long areaId,
			RedirectAttributes redirectAttributes) {
		receiver.setArea(this.areaService.find(areaId));
		if (!validate(receiver, new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if ((Receiver.MAX_RECEIVER_COUNT != null)
				&& (member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT
						.intValue())) {
			return SHOP_COMMON_ERROR;
		}

		receiver.setMember(member);
		this.receiverService.save(receiver);
		setRedirect(redirectAttributes, SHOP_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model,
			RedirectAttributes redirectAttributes) {
		Receiver receiver = this.receiverService.find(id);
		if (receiver == null) {
			return SHOP_COMMON_ERROR;
		}

		Member localMember = this.memberService.getCurrent();
		if (receiver.getMember() != localMember) {
			return SHOP_COMMON_ERROR;
		}

		model.addAttribute("receiver", receiver);

		return "shop/member/receiver/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Receiver receiver, Long id, Long areaId,
			RedirectAttributes redirectAttributes) {
		receiver.setArea((Area) this.areaService.find(areaId));
		if (!validate(receiver, new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		Receiver rv = this.receiverService.find(id);
		if (rv == null) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (rv.getMember() != member) {
			return SHOP_COMMON_ERROR;
		}

		this.receiverService.update(receiver, new String[] { "member" });
		setRedirect(redirectAttributes, SHOP_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long id) {
		Receiver receiver = (Receiver) this.receiverService.find(id);
		if (receiver == null) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (receiver.getMember() != member) {
			return SHOP_MESSAGE_ERROR;
		}

		this.receiverService.delete(id);

		return SHOP_MESSAGE_SUCCESS;
	}
}