package com.korres.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.Brand;
import com.korres.entity.Consultation;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.service.ConsultationService;
import com.korres.service.MemberService;
import com.korres.service.ProductService;
import com.korres.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import com.korres.Filter;
import com.korres.Order;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("consultationListDirective")
public class ConsultationListDirective extends BaseDirective {
	private static final String MEMBER_ID = "memberId";
	private static final String PRODUCT_ID = "productId";
	private static final String CONSULTATIONS = "consultations";

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Long memberId = (Long) FreemarkerUtils.getParameter(MEMBER_ID,
				Long.class, params);
		Long productId = (Long) FreemarkerUtils.getParameter(PRODUCT_ID,
				Long.class, params);
		Member member = this.memberService.find(memberId);
		Product product = this.productService.find(productId);
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		Integer count = this.getCount(params);
		List<Filter> lif = getFilters(params, Brand.class, new String[0]);
		List<Order> lio = getOrders(params, new String[0]);
		List<Consultation> lic = null;
		if (((memberId != null) && (member == null))
				|| ((productId != null) && (product == null))) {
			lic = new ArrayList<Consultation>();
		} else if (bool) {
			lic = this.consultationService.findList(member, product, Boolean
					.valueOf(true), count, lif, lio, cacheRegion);
		} else {
			lic = this.consultationService.findList(member, product, Boolean
					.valueOf(true), count, lif, lio);
		}

		setVariables(CONSULTATIONS, lic, env, body);
	}
}