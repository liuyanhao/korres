package com.korres.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.korres.entity.Coupon;
import com.korres.entity.Coupon.CouponOperator;
import com.korres.entity.CouponCode;
import com.korres.service.AdminService;
import com.korres.service.CouponCodeService;
import com.korres.service.CouponService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.ExcelView;
import com.korres.Message;
import com.korres.Pageable;

@Controller("adminCouponController")
@RequestMapping({ "/admin/coupon" })
public class CouponController extends BaseController {

	@Resource(name = "couponServiceImpl")
	private CouponService couponService;

	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("operators", CouponOperator.values());
		return "/admin/coupon/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Coupon coupon, RedirectAttributes redirectAttributes) {
		if (!validator(coupon, new Class[0])){
			return "/admin/common/error";
		}
		
		if ((coupon.getBeginDate() != null) && (coupon.getEndDate() != null)
				&& (coupon.getBeginDate().after(coupon.getEndDate()))){
			return "/admin/common/error";
		}
		
		if ((coupon.getStartPrice() != null) && (coupon.getEndPrice() != null)
				&& (coupon.getStartPrice().compareTo(coupon.getEndPrice()) > 0)){
			return "/admin/common/error";
		}
		
		if ((coupon.getIsExchange().booleanValue())
				&& (coupon.getPoint() == null)){
			return "/admin/common/error";
		}
		
		if ((coupon.getPriceOperator() == CouponOperator.divide)
				&& (coupon.getPriceValue() != null)
				&& (coupon.getPriceValue().compareTo(new BigDecimal(0)) == 0)){
			return "/admin/common/error";
		}
		
		if (!coupon.getIsExchange().booleanValue()){
			coupon.setPoint(null);
		}
		
		coupon.setCouponCodes(null);
		coupon.setPromotions(null);
		coupon.setOrders(null);
		this.couponService.save(coupon);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("coupon", this.couponService.find(id));
		model.addAttribute("operators", CouponOperator.values());
		
		return "/admin/coupon/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Coupon coupon, RedirectAttributes redirectAttributes) {
		if (!validator(coupon, new Class[0])){
			return "/admin/common/error";
		}
		
		if ((coupon.getBeginDate() != null) && (coupon.getEndDate() != null)
				&& (coupon.getBeginDate().after(coupon.getEndDate()))){
			return "/admin/common/error";
		}
		
		if ((coupon.getStartPrice() != null) && (coupon.getEndPrice() != null)
				&& (coupon.getStartPrice().compareTo(coupon.getEndPrice()) > 0)){
			return "/admin/common/error";
		}
		
		if ((coupon.getIsExchange().booleanValue())
				&& (coupon.getPoint() == null)){
			return "/admin/common/error";
		}
		
		if ((coupon.getPriceOperator() == CouponOperator.divide)
				&& (coupon.getPriceValue() != null)
				&& (coupon.getPriceValue().compareTo(new BigDecimal(0)) == 0)){
			return "/admin/common/error";
		}
		
		if (!coupon.getIsExchange().booleanValue()){
			coupon.setPoint(null);
		}
		
		this.couponService.update(coupon, new String[] { "couponCodes",
				"promotions", "orders" });
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.couponService.findPage(pageable));
		
		return "/admin/coupon/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.couponService.delete(ids);
		
		return ADMIN_MESSAGE_SUCCESS;
	}

	@RequestMapping(value = { "/build" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String build(Long id, ModelMap model) {
		Coupon coupon = this.couponService.find(id);
		model.addAttribute("coupon", coupon);
		model.addAttribute("totalCount", this.couponCodeService.count(
				coupon, null, null, null, null));
		model.addAttribute("usedCount", this.couponCodeService.count(
				coupon, null, null, null, Boolean.valueOf(true)));
		
		return "/admin/coupon/build";
	}

	@RequestMapping(value = { "/download" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView download(Long id, Integer count, ModelMap model) {
		if ((count == null) || (count.intValue() <= 0)){
			count = Integer.valueOf(50);
		}
		
		Coupon coupon = (Coupon) this.couponService.find(id);
		List<CouponCode>  list = this.couponCodeService.build(coupon, null, count);
		String str = "coupon_code_"
				+ new SimpleDateFormat("yyyyMM").format(new Date()) + ".xls";
		String[] arrayOfString = new String[4];
		arrayOfString[0] = (getMessage("admin.coupon.type", new Object[0]) + ": " + coupon
				.getName());
		arrayOfString[1] = (getMessage("admin.coupon.count", new Object[0])
				+ ": " + count);
		arrayOfString[2] = (getMessage("admin.coupon.operator", new Object[0])
				+ ": " + this.adminService.getCurrentUsername());
		arrayOfString[3] = (getMessage("admin.coupon.date", new Object[0]) + ": " + new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date()));
		
		return new ModelAndView(new ExcelView(str, null,
				new String[] { "code" }, new String[] { getMessage(
						"admin.coupon.title", new Object[0]) }, null, null,
						list, arrayOfString), model);
	}
}