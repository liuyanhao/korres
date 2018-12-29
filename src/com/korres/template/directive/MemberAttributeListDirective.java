package com.korres.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.korres.entity.MemberAttribute;
import com.korres.service.MemberAttributeService;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("memberAttributeListDirective")
public class MemberAttributeListDirective extends BaseDirective {
	private static final String MEMBER_ATTRIBUTES = "memberAttributes";

	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = useCache(env, params);
		String cacheRegion = cacheRegion(env, params);
		List<MemberAttribute> lima = null;
		if (bool) {
			lima = this.memberAttributeService.findList(cacheRegion);
		} else {
			lima = this.memberAttributeService.findList();
		}

		setVariables(MEMBER_ATTRIBUTES, lima, env, body);
	}
}