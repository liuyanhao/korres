package com.korres.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

@Controller("adminMessageController")
@RequestMapping( { "/admin/message" })
public class MessageController extends BaseController {

	@Resource(name = "messageServiceImpl")
	MessageService messageService;

	@Resource(name = "memberServiceImpl")
	MemberService memberService;

	@RequestMapping(value = { "/check_username" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkUsername(String username) {
		return this.memberService.usernameExists(username);
	}

	@RequestMapping(value = { "/send" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String send(Long draftMessageId, Model model) {
		com.korres.entity.Message message = (com.korres.entity.Message) this.messageService
				.find(draftMessageId);
		if ((message != null) && (message.getIsDraft().booleanValue())
				&& (message.getSender() == null)) {
			model.addAttribute("draftMessage", message);
		}

		return "admin/message/send";
	}

	@RequestMapping(value = { "/send" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String send(Long draftMessageId, String username, String title,
			String content,
			@RequestParam(defaultValue = "false") Boolean isDraft,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!validator(com.korres.entity.Message.class, "content", content,
				new Class[0])) {
			return "/admin/common/error";
		}

		com.korres.entity.Message message = (com.korres.entity.Message) this.messageService
				.find(draftMessageId);
		if ((message != null) && (message.getIsDraft().booleanValue())
				&& (message.getSender() == null)) {
			this.messageService.delete(message);
		}

		Member member = null;
		if (StringUtils.isNotEmpty(username)) {
			member = this.memberService.findByUsername(username);
			if (member == null) {
				return "/admin/common/error";
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
		msg.setSender(null);
		msg.setReceiver(member);
		msg.setForMessage(null);
		msg.setReplyMessages(null);
		this.messageService.save(msg);

		if (isDraft.booleanValue()) {
			setRedirectAttributes(redirectAttributes, com.korres.Message.success(
					"admin.message.saveDraftSuccess", new Object[0]));
			return "redirect:draft.jhtml";
		}

		setRedirectAttributes(redirectAttributes, com.korres.Message.success(
				"admin.message.sendSuccess", new Object[0]));

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, Model model) {
		com.korres.entity.Message adminMessage = (com.korres.entity.Message) this.messageService
				.find(id);
		if ((adminMessage == null)
				|| (adminMessage.getIsDraft().booleanValue())
				|| (adminMessage.getForMessage() != null)) {
			return "/admin/common/error";
		}

		if (((adminMessage.getSender() != null) && (adminMessage.getReceiver() != null))
				|| ((adminMessage.getReceiver() == null) && (adminMessage
						.getReceiverDelete().booleanValue()))
				|| ((adminMessage.getSender() == null) && (adminMessage
						.getSenderDelete().booleanValue()))) {
			return "/admin/common/error";
		}

		if (adminMessage.getReceiver() == null) {
			adminMessage.setReceiverRead(Boolean.valueOf(true));
		} else {
			adminMessage.setSenderRead(Boolean.valueOf(true));
		}

		this.messageService.update(adminMessage);
		model.addAttribute("adminMessage", adminMessage);

		return "/admin/message/view";
	}

	@RequestMapping(value = { "/reply" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String reply(Long id, String content, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (!validator(com.korres.entity.Message.class, "content", content,
				new Class[0])) {
			return "/admin/common/error";
		}

		com.korres.entity.Message message = this.messageService.find(id);
		if ((message == null) || (message.getIsDraft().booleanValue())
				|| (message.getForMessage() != null)) {
			return "/admin/common/error";
		}

		if (((message.getSender() != null) && (message.getReceiver() != null))
				|| ((message.getReceiver() == null) && (message
						.getReceiverDelete().booleanValue()))
				|| ((message.getSender() == null) && (message.getSenderDelete()
						.booleanValue()))) {
			return "/admin/common/error";
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
		msg.setSender(null);
		msg.setReceiver(message.getReceiver() == null ? message.getSender()
				: message.getReceiver());

		if (((message.getReceiver() == null) && (!message.getSenderDelete()
				.booleanValue()))
				|| ((message.getSender() == null) && (!message
						.getReceiverDelete().booleanValue()))) {
			msg.setForMessage(message);
		}

		msg.setReplyMessages(null);
		this.messageService.save(msg);

		if (message.getSender() == null) {
			message.setSenderRead(Boolean.valueOf(true));
			message.setReceiverRead(Boolean.valueOf(false));
		} else {
			message.setSenderRead(Boolean.valueOf(false));
			message.setReceiverRead(Boolean.valueOf(true));
		}

		this.messageService.update(message);
		if (((message.getReceiver() == null) && (!message.getSenderDelete()
				.booleanValue()))
				|| ((message.getSender() == null) && (!message
						.getReceiverDelete().booleanValue()))) {
			setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
			return "redirect:view.jhtml?id=" + message.getId();
		}

		setRedirectAttributes(redirectAttributes, com.korres.Message.success(
				"admin.message.replySuccess", new Object[0]));

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, Model model) {
		model
				.addAttribute("page", this.messageService.findPage(null,
						pageable));
		return "/admin/message/list";
	}

	@RequestMapping(value = { "/draft" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String draft(Pageable pageable, Model model) {
		model.addAttribute("page", this.messageService.findDraftPage(null,
				pageable));

		return "/admin/message/draft";
	}

	@RequestMapping(value = { "delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public com.korres.Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				this.messageService.delete(id, null);
			}
		}

		return ADMIN_MESSAGE_SUCCESS;
	}
}