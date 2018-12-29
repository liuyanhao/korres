package com.korres.controller.admin;

import javax.annotation.Resource;

import com.korres.entity.Role;
import com.korres.service.RoleService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminRoleController")
@RequestMapping({ "/admin/role" })
public class RoleController extends BaseController {

	@Resource(name = "roleServiceImpl")
	private RoleService roleService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add() {
		return "/admin/role/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Role role, RedirectAttributes redirectAttributes) {
		if (!validator(role, new Class[0])){
			return "/admin/common/error";
		}
		
		role.setIsSystem(Boolean.valueOf(false));
		role.setAdmins(null);
		this.roleService.save(role);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("role", this.roleService.find(id));
		
		return "/admin/role/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Role role, RedirectAttributes redirectAttributes) {
		if (!validator(role, new Class[0])){
			return "/admin/common/error";
		}
		
		Role localRole = (Role) this.roleService.find(role.getId());
		if ((localRole == null) || (localRole.getIsSystem().booleanValue())){
			return "/admin/common/error";
		}
		
		this.roleService.update(role, new String[] { "isSystem", "admins" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.roleService.findPage(pageable));
		
		return "/admin/role/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long localLong : ids) {
				Role localRole = (Role) this.roleService.find(localLong);
				if ((localRole != null)
						&& ((localRole.getIsSystem().booleanValue()) || ((localRole
								.getAdmins() != null) && (!localRole
								.getAdmins().isEmpty())))){
					return Message.error("admin.role.deleteExistNotAllowed",
							new Object[] { localRole.getName() });
				}
			}
			this.roleService.delete(ids);
		}
		
		return ADMIN_MESSAGE_SUCCESS;
	}
}