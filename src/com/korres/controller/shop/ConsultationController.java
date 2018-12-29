package com.korres.controller.shop;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.entity.Consultation;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.service.CaptchaService;
import com.korres.service.ConsultationService;
import com.korres.service.MemberService;
import com.korres.service.ProductService;
import com.korres.util.SettingUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Message;
import com.korres.Pageable;
import com.korres.ResourceNotFoundException;
import com.korres.Setting;

@Controller("shopConsultationController")
@RequestMapping({ "/consultation" })
public class ConsultationController extends BaseController {
	private static final int  PAGE_SIZE = 10;

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@RequestMapping(value = { "/add/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(@PathVariable Long id, ModelMap model) {
		Setting setting = SettingUtils.get();
		if (!setting.getIsConsultationEnabled().booleanValue()) {
			throw new ResourceNotFoundException();
		}

		Product product = (Product) this.productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}

		model.addAttribute("product", product);
		model.addAttribute("captchaId", UUID.randomUUID().toString());

		return "/shop/consultation/add";
	}

	@RequestMapping(value = { "/content/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String content(@PathVariable Long id, Integer pageNumber,
			ModelMap model) {
		Setting setting = SettingUtils.get();
		if (!setting.getIsConsultationEnabled().booleanValue()) {
			throw new ResourceNotFoundException();
		}

		Product product = (Product) this.productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}

		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("product", product);
		model.addAttribute(
				"page",
				this.consultationService.findPage(null, product,
						Boolean.valueOf(true), pageable));

		return "/shop/consultation/content";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message save(String captchaId, String captcha, Long id,
			String content, HttpServletRequest request) {
		if (!this.captchaService.isValid(Setting.CaptchaType.consultation,
				captchaId, captcha)) {
			return Message.error("shop.captcha.invalid", new Object[0]);
		}

		Setting setting = SettingUtils.get();
		if (!setting.getIsConsultationEnabled().booleanValue()) {
			return Message.error("shop.consultation.disabled", new Object[0]);
		}

		if (!validate(Consultation.class, "content", content, new Class[0])) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if ((setting.getConsultationAuthority() != Setting.ConsultationAuthority.anyone)
				&& (member == null)) {
			return Message.error("shop.consultation.accessDenied",
					new Object[0]);
		}

		Product product = (Product) this.productService.find(id);
		if (product == null) {
			return SHOP_MESSAGE_ERROR;
		}

		Consultation consultation = new Consultation();
		consultation.setContent(content);
		consultation.setIp(request.getRemoteAddr());
		consultation.setMember(member);
		consultation.setProduct(product);
		if (setting.getIsConsultationCheck().booleanValue()) {
			consultation.setIsShow(Boolean.valueOf(false));
			this.consultationService.save(consultation);

			return Message.success("shop.consultation.check", new Object[0]);
		}

		consultation.setIsShow(Boolean.valueOf(true));
		this.consultationService.save(consultation);

		return Message.success("shop.consultation.success", new Object[0]);
	}
}