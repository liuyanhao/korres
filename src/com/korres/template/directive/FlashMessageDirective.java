package com.korres.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.korres.Message;

@Component("flashMessageDirective")
public class FlashMessageDirective extends BaseDirective {
	public static final String FLASH_MESSAGE_ATTRIBUTE_NAME = FlashMessageDirective.class
			.getName()
			+ ".FLASH_MESSAGE";
	private static final String FLASH_MESSAGE = "flashMessage";

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		RequestAttributes localRequestAttributes = RequestContextHolder
				.currentRequestAttributes();
		if (localRequestAttributes != null) {
			Message message = (Message) localRequestAttributes
					.getAttribute(FLASH_MESSAGE_ATTRIBUTE_NAME, 0);
			if (body != null) {
				setVariables(FLASH_MESSAGE, message, env, body);
			} else if (message != null) {
				Writer writer = env.getOut();
				writer.write("$.message(\"" + message.getType()
						+ "\", \"" + message.getContent() + "\");");
			}
		}
	}
}