package com.korres.template.directive;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.korres.entity.AdPosition;
import com.korres.service.AdPositionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Component("adPositionDirective")
public class AdPositionDirective extends BaseDirective {
	private static final String AD_POSITION = "adPosition";

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Resource(name = "adPositionServiceImpl")
	private AdPositionService adPositionService;

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) {
		Long id = null;
		try {
			id = getId(params);
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		
		boolean bool = false;
		try {
			bool = useCache(env, params);
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		
		String str = null;
		try {
			str = cacheRegion(env, params);
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}

		AdPosition adPosition = null;
		if (bool) {
			adPosition = this.adPositionService.find(id, str);
		} else {
			adPosition = (AdPosition) this.adPositionService.find(id);
		}

		if (body != null) {
			try {
				setVariables(AD_POSITION, adPosition, env, body);
			} catch (TemplateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((adPosition != null) && (adPosition.getTemplate() != null)) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(AD_POSITION, adPosition);
				Writer writer = env.getOut();
				new Template("adTemplate", new StringReader(adPosition
						.getTemplate()), this.freeMarkerConfigurer
						.getConfiguration()).process(map, writer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}