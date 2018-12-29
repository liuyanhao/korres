package com.korres.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.Map;
import com.korres.interceptor.ExecuteTimeInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component("executeTimeDirective")
public class ExecuteTimeDirective extends BaseDirective {
	private static final String EXECUTE_TIME = "executeTime";

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		RequestAttributes localRequestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (localRequestAttributes != null) {
			Long localLong = (Long) localRequestAttributes.getAttribute(
					ExecuteTimeInterceptor.EXECUTE_TIME, 0);
			if (localLong != null)
				setVariables(EXECUTE_TIME, localLong, env, body);
		}
	}
}