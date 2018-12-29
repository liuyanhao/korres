package com.korres.controller.shop.member;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.service.MemberService;
import com.korres.service.MessageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Pageable;

@Controller("shopMemberMessageController")
@RequestMapping( { "/member/message" })
public class MessageController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "messageServiceImpl")
	private MessageService messageService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@RequestMapping(value = { "/check_username" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkUsername(String username) {
		return (!StringUtils.equalsIgnoreCase(username, this.memberService
				.getCurrentUsername()))
				&& (this.memberService.usernameExists(username));
	}

	@RequestMapping(value = { "/send" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String send(Long draftMessageId, Model model) {
		com.korres.entity.Message message = (com.korres.entity.Message) this.messageService
				.find(draftMessageId);
		if ((message != null) && (message.getIsDraft().booleanValue())
				&& (message.getSender() == this.memberService.getCurrent())) {
			model.addAttribute("draftMessage", message);
		}

		return "/shop/member/message/send";
	}

	@RequestMapping(value = { "/send" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String send(Long draftMessageId, String username, String title,
			String content,
			@RequestParam(defaultValue = "false") Boolean isDraft,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!validate(com.korres.entity.Message.class, "content", content,
				new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		com.korres.entity.Message message = (com.korres.entity.Message) this.messageService
				.find(draftMessageId);
		if ((message != null) && (message.getIsDraft().booleanValue())
				&& (message.getSender() == member)) {
			this.messageService.delete(message);
		}

		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = this.memberService.findByUsername(username);
			if ((receiver == null) || (receiver == member)) {
				return SHOP_COMMON_ERROR;
			}
		}

		com.korres.entity.Message msg = new com.korres.entity.Message();
		msg.setTitle(title);
		msg.setContent(content);
		msg.setIp(request.getRemoteAddr());
		msg.setIsDraft(isDraft);
		msg.setSenderRead(Boolean.valueOf(true));
		msg.setReceiverRead(Boolean.valueOf(false));
		msg.setSenderDelete(Boolean.valueOf(false));
		msg.setReceiverDelete(Boolean.valueOf(false));
		msg.setSender(member);
		msg.setReceiver(receiver);
		this.messageService.save(msg);

		if (isDraft.booleanValue()) {
			setRedirect(redirectAttributes, com.korres.Message.success(
					"shop.member.message.saveDraftSuccess", new Object[0]));
			return "redirect:draft.jhtml";
		}

		setRedirect(redirectAttributes, com.korres.Message.success(
				"shop.member.message.sendSuccess", new Object[0]));

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, Model model) {
		com.korres.entity.Message message = (com.korres.entity.Message) this.messageService
				.find(id);
		if ((message == null) || (message.getIsDraft().booleanValue())
				|| (message.getForMessage() != null)) {
			return SHOP_COMMON_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if (((message.getSender() != member) && (message.getReceiver() != member))
				|| ((message.getReceiver() == member) && (message
						.getReceiverDelete().booleanValue()))
				|| ((message.getSender() == member) && (message
						.getSenderDelete().booleanValue())))
			return SHOP_COMMON_ERROR;
		if (member == message.getReceiver()) {
			message.setReceiverRead(Boolean.valueOf(true));
		} else {
			message.setSenderRead(Boolean.valueOf(true));
		}

		this.messageService.update(message);
		model.addAttribute("memberMessage", message);

		return "/shop/member/message/view";
	}

	@RequestMapping(value = { "/reply" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String reply(Long id, String content, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (!validate(com.korres.entity.Message.class, "content", content,
				new Class[0])) {
			return SHOP_COMMON_ERROR;
		}

		com.korres.entity.Message message = (com.korres.entity.Message) this.messageService
				.find(id);
		if ((message == null) || (message.getIsDraft().booleanValue())
				|| (message.getForMessage() != null)) {
			return SHOP_COMMON_ERROR;
		}

		Member localMember = this.memberService.getCurrent();
		if (((message.getSender() != localMember) && (message.getReceiver() != localMember))
				|| ((message.getReceiver() == localMember) && (message
						.getReceiverDelete().booleanValue()))
				|| ((message.getSender() == localMember) && (message
						.getSenderDelete().booleanValue()))) {
			return SHOP_COMMON_ERROR;
		}

		com.korres.entity.Message msg = new com.korres.entity.Message();
		msg.setTitle("reply: " + message.getTitle());
		msg.setContent(content);
		msg.setIp(request.getRemoteAddr());
		msg.setIsDraft(Boolean.valueOf(false));
		msg.setSenderRead(Boolean.valueOf(true));
		msg.setReceiverRead(Boolean.valueOf(false));
		msg.setSenderDelete(Boolean.valueOf(false));
		msg.setReceiverDelete(Boolean.valueOf(false));
		msg.setSender(localMember);
		msg.setReceiver(localMember == message.getReceiver() ? message
				.getSender() : message.getReceiver());
		if (((localMember == message.getReceiver()) && (!message
				.getSenderDelete().booleanValue()))
				|| ((localMember == message.getSender()) && (!message
						.getReceiverDelete().booleanValue()))) {
			msg.setForMessage(message);
		}

		this.messageService.save(msg);
		if (localMember.equals(message.getSender())) {
			message.setSenderRead(Boolean.valueOf(true));
			message.setReceiverRead(Boolean.valueOf(false));
		} else {
			message.setSenderRead(Boolean.valueOf(false));
			message.setReceiverRead(Boolean.valueOf(true));
		}

		this.messageService.update(message);
		if (((localMember == message.getReceiver()) && (!message
				.getSenderDelete().booleanValue()))
				|| ((localMember == message.getSender()) && (!message
						.getReceiverDelete().booleanValue()))) {
			setRedirect(redirectAttributes, SHOP_MESSAGE_SUCCESS);
			return "redirect:view.jhtml?id=" + message.getId();
		}

		setRedirect(redirectAttributes, com.korres.Message.success(
				"shop.member.message.replySuccess", new Object[0]));

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, Model model) {
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		Member member = this.memberService.getCurrent();
		model.addAttribute("page", this.messageService.findPage(member,
				pageable));

		return "/shop/member/message/list";
	}

	@RequestMapping(value = { "/draft" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String draft(Integer pageNumber, Model model) {
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		Member member = this.memberService.getCurrent();
		model.addAttribute("page", this.messageService.findDraftPage(member,
				pageable));

		return "/shop/member/message/draft";
	}

	@RequestMapping(value = { "delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public com.korres.Message delete(Long id) {
		Member member = this.memberService.getCurrent();
		this.messageService.delete(id, member);

		return SHOP_MESSAGE_SUCCESS;
	}
}