package com.korres.controller.shop.member;

import javax.annotation.Resource;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.service.ConsultationService;
import com.korres.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;

/*
 * 类名：ConsultationController.java
 * 功能说明：商品咨询
 * 创建日期：2013-12-20 下午04:38:30
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Controller("shopMemberConsultationController")
@RequestMapping( { "/member/consultation" })
public class ConsultationController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;

	/**
	 * 商品咨询列表
	 * @param pageNumber
	 * @param model
	 * @return
	 * @author weiyuanhua
	 * @date 2013-12-20 下午04:46:09
	 */
	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.consultationService.findPage(member,
				null, null, pageable));
		return "shop/member/consultation/list";
	}
}