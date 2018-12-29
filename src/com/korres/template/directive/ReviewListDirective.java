package com.korres.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.Review;
import com.korres.entity.Review.ReviewType;
import com.korres.service.MemberService;
import com.korres.service.ProductService;
import com.korres.service.ReviewService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("reviewListDirective")
public class ReviewListDirective extends BaseDirective {
	private static final String IIIllIlI = "memberId";
	private static final String IIIllIll = "productId";
	private static final String IIIlllII = "type";
	private static final String IIIlllIl = "reviews";

	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Long memberId = (Long) FreemarkerUtils.getParameter("memberId",
				Long.class, params);
		Long productId = (Long) FreemarkerUtils.getParameter("productId",
				Long.class, params);
		ReviewType type = (ReviewType) FreemarkerUtils.getParameter("type",
				ReviewType.class, params);
		Member member = this.memberService.find(memberId);
		Product product = this.productService.find(productId);
		Object localObject;
		if (((memberId != null) && (member == null))
				|| ((productId != null) && (product == null))) {
			localObject = new ArrayList();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = cacheRegion(env, params);
			Integer count = this.getCount(params);
			List<Filter> lif = getFilters(params, Review.class, new String[0]);
			List<Order> lio = getOrders(params, new String[0]);
			if (useCache) {
				localObject = this.reviewService.findList(member, product,
						type, Boolean.valueOf(true), count, lif, lio,
						cacheRegion);
			} else {
				localObject = this.reviewService.findList(member, product,
						type, Boolean.valueOf(true), count, lif, lio);
			}
		}

		setVariables("reviews", localObject, env, body);
	}
}