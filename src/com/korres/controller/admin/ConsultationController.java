package com.korres.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.korres.entity.Consultation;
import com.korres.service.ConsultationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminConsultationController")
@RequestMapping({ "/admin/consultation" })
public class ConsultationController extends BaseController {

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;

	@RequestMapping(value = { "/reply" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String reply(Long id, ModelMap model) {
		model.addAttribute("consultation", this.consultationService.find(id));

		return "/admin/consultation/reply";
	}

	@RequestMapping(value = { "/reply" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String reply(Long id, String content, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (!validator(Consultation.class, "content", content, new Class[0]))
			return "/admin/common/error";
		Consultation consultation1 = this.consultationService.find(id);

		if (consultation1 == null) {
			return "/admin/common/error";
		}

		Consultation consultation2 = new Consultation();
		consultation2.setContent(content);
		consultation2.setIp(request.getRemoteAddr());

		this.consultationService.reply(consultation1, consultation2);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:reply.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("consultation", this.consultationService.find(id));

		return "/admin/consultation/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Long id,
			@RequestParam(defaultValue = "false") Boolean isShow,
			RedirectAttributes redirectAttributes) {
		Consultation consultation = this.consultationService.find(id);

		if (consultation == null) {
			return "/admin/common/error";
		}

		if (isShow != consultation.getIsShow()) {
			consultation.setIsShow(isShow);
			this.consultationService.update(consultation);
		}

		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page",
				this.consultationService.findPage(null, null, null, pageable));

		return "/admin/consultation/list";
	}

	@RequestMapping(value = { "/delete_reply" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message deleteReply(Long id) {
		Consultation consultation = this.consultationService.find(id);
		if ((consultation == null)
				|| (consultation.getForConsultation() == null)) {
			return ADMIN_MESSAGE_ERROR;
		}

		this.consultationService.delete(consultation);

		return ADMIN_MESSAGE_SUCCESS;
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			this.consultationService.delete(ids);
		}

		return ADMIN_MESSAGE_SUCCESS;
	}
}