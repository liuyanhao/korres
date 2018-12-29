package com.korres.controller.admin;

import javax.annotation.Resource;

import com.korres.entity.Review;
import com.korres.entity.Review.ReviewType;
import com.korres.service.ReviewService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.Message;
import com.korres.Pageable;

@Controller("adminReviewController")
@RequestMapping( { "/admin/review" })
public class ReviewController extends BaseController {

	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("review", this.reviewService.find(id));

		return "/admin/review/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Long id,
			@RequestParam(defaultValue = "false") Boolean isShow,
			RedirectAttributes redirectAttributes) {
		Review review = (Review) this.reviewService.find(id);
		if (review == null)
			return "/admin/common/error";
		review.setIsShow(isShow);
		this.reviewService.update(review);
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);

		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ReviewType type, Pageable pageable, ModelMap model) {
		model.addAttribute("type", type);
		model.addAttribute("types", ReviewType.values());
		model.addAttribute("page", this.reviewService.findPage(null, null,
				type, null, pageable));

		return "/admin/review/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.reviewService.delete(ids);

		return ADMIN_MESSAGE_SUCCESS;
	}
}