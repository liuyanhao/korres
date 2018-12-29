package com.korres.controller.admin;

import java.util.Date;
import javax.annotation.Resource;
import com.korres.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;

@Controller("adminPurchaseRankingController")
@RequestMapping( { "/admin/purchase_ranking" })
public class PurchaseRankingController extends BaseController {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Date beginDate, Date endDate, Pageable pageable,
			Model model) {
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("page", this.memberService.findPurchasePage(
				beginDate, endDate, pageable));

		return "/admin/purchase_ranking/list";
	}
}