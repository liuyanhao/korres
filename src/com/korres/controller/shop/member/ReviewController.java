package com.korres.controller.shop.member;

import javax.annotation.Resource;
import com.korres.controller.shop.BaseController;
import com.korres.entity.Member;
import com.korres.service.MemberService;
import com.korres.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;

@Controller("shopMemberReviewController")
@RequestMapping( { "/member/review" })
public class ReviewController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Integer pageNumber, ModelMap model) {
		Member member = this.memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, Integer.valueOf(PAGE_SIZE));
		model.addAttribute("page", this.reviewService.findPage(member, null,
				null, null, pageable));

		return "shop/member/review/list";
	}
}