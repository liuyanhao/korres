package com.korres.controller.shop;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Review;
import com.korres.service.CaptchaService;
import com.korres.service.MemberService;
import com.korres.service.ProductService;
import com.korres.service.ReviewService;
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

@Controller("shopReviewController")
@RequestMapping({ "/review" })
public class ReviewController extends BaseController {
	private static final int PAGE_SIZE = 10;

	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@RequestMapping(value = { "/add/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(@PathVariable Long id, ModelMap model) {
		Setting setting = SettingUtils.get();
		if (!setting.getIsReviewEnabled().booleanValue()) {
			throw new ResourceNotFoundException();
		}

		Product product = this.productService.find(id);
		if (product == null) {
			throw new ResourceNotFoundException();
		}

		model.addAttribute("product", product);
		model.addAttribute("captchaId", UUID.randomUUID().toString());

		return "/shop/review/add";
	}

	@RequestMapping(value = { "/content/{id}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String content(@PathVariable Long id, Integer pageNumber,
			ModelMap model) {
		Setting setting = SettingUtils.get();
		if (!setting.getIsReviewEnabled().booleanValue()) {
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
				this.reviewService.findPage(null, product, null,
						Boolean.valueOf(true), pageable));

		return "/shop/review/content";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message save(String captchaId, String captcha, Long id,
			Integer score, String content, HttpServletRequest request) {
		if (!this.captchaService.isValid(Setting.CaptchaType.review, captchaId,
				captcha)) {
			return Message.error("shop.captcha.invalid", new Object[0]);
		}

		Setting setting = SettingUtils.get();
		if (!setting.getIsReviewEnabled().booleanValue()) {
			return Message.error("shop.review.disabled", new Object[0]);
		}

		if ((!validate(Review.class, "score", score, new Class[0]))
				|| (!validate(Review.class, "content", content, new Class[0]))) {
			return SHOP_MESSAGE_ERROR;
		}

		Product product = (Product) this.productService.find(id);
		if (product == null) {
			return SHOP_MESSAGE_ERROR;
		}

		Member member = this.memberService.getCurrent();
		if ((setting.getReviewAuthority() != Setting.ReviewAuthority.anyone)
				&& (member == null)) {
			return Message.error("shop.review.accessDenied", new Object[0]);
		}

		if (setting.getReviewAuthority() == Setting.ReviewAuthority.purchased) {
			if (!this.productService.isPurchased(member, product)) {
				return Message.error("shop.review.noPurchased", new Object[0]);
			}

			if (this.reviewService.isReviewed(member, product)) {
				return Message.error("shop.review.reviewed", new Object[0]);
			}
		}

		Review review = new Review();
		review.setScore(score);
		review.setContent(content);
		review.setIp(request.getRemoteAddr());
		review.setMember(member);
		review.setProduct(product);

		if (setting.getIsReviewCheck().booleanValue()) {
			review.setIsShow(Boolean.valueOf(false));
			this.reviewService.save(review);
			return Message.success("shop.review.check", new Object[0]);
		}

		review.setIsShow(Boolean.valueOf(true));
		this.reviewService.save(review);

		return Message.success("shop.review.success", new Object[0]);
	}
}