package com.korres.controller.admin;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import com.korres.entity.MemberAttribute;
import com.korres.entity.BaseEntity.BaseEntitySave;
import com.korres.entity.MemberAttribute.MemberAttributeType;
import com.korres.service.MemberAttributeService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminMemberAttributeController")
@RequestMapping( { "/admin/member_attribute" })
public class MemberAttributeController extends BaseController {

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model, RedirectAttributes redirectAttributes) {
		if (this.memberAttributeService.count() - 8L >= 10L) {
			setRedirectAttributes(redirectAttributes, Message.warn(
					"admin.memberAttribute.addCountNotAllowed",
					new Object[] { Integer.valueOf(10) }));
		}

		return "/admin/member_attribute/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(MemberAttribute memberAttribute,
			RedirectAttributes redirectAttributes) {
		if (!validator(memberAttribute, new Class[] { BaseEntitySave.class })) {
			return "/admin/common/error";
		}

		if ((memberAttribute.getType() == MemberAttributeType.select)
				|| (memberAttribute.getType() == MemberAttributeType.checkbox)) {
			List<String> list = memberAttribute.getOptions();
			if (list != null) {
				Iterator<String> iterator = list.iterator();
				while (iterator.hasNext()) {
					String str = iterator.next();
					if (StringUtils.isEmpty(str)) {
						iterator.remove();
					}
				}
			}

			if ((list == null) || (list.isEmpty())) {
				return "/admin/common/error";
			}
		} else if (memberAttribute.getType() == MemberAttributeType.text) {
			memberAttribute.setOptions(null);
		} else {
			return "/admin/common/error";
		}

		Integer index = this.memberAttributeService.findUnusedPropertyIndex();
		if (index == null) {
			return "/admin/common/error";
		}

		memberAttribute.setPropertyIndex(index);
		this.memberAttributeService.save(memberAttribute);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("memberAttribute", this.memberAttributeService
				.find(id));

		return "/admin/member_attribute/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(MemberAttribute memberAttribute,
			RedirectAttributes redirectAttributes) {
		if (!validator(memberAttribute, new Class[0])) {
			return "/admin/common/error";
		}

		MemberAttribute ma = (MemberAttribute) this.memberAttributeService
				.find(memberAttribute.getId());
		if (ma == null) {
			return "/admin/common/error";
		}

		if ((ma.getType() == MemberAttributeType.select)
				|| (ma.getType() == MemberAttributeType.checkbox)) {
			List<String> list = memberAttribute.getOptions();
			if (list != null) {
				Iterator<String> iterator = list.iterator();
				while (iterator.hasNext()) {
					String str = iterator.next();
					if (StringUtils.isEmpty(str)) {
						iterator.remove();
					}
				}
			}

			if ((list == null) || (list.isEmpty())) {
				return "/admin/common/error";
			}
		} else {
			memberAttribute.setOptions(null);
		}

		this.memberAttributeService.update(memberAttribute, new String[] {
				"type", "propertyIndex" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.memberAttributeService
				.findPage(pageable));

		return "/admin/member_attribute/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.memberAttributeService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}