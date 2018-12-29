package com.korres.controller.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.DateEditor;
import com.korres.HtmlCleanEditor;
import com.korres.Message;
import com.korres.Setting;
import com.korres.template.directive.FlashMessageDirective;
import com.korres.util.SettingUtils;
import com.korres.util.SpringUtils;

public class BaseController {
	protected static final String SHOP_COMMON_ERROR = "/shop/common/error";
	protected static final Message SHOP_MESSAGE_ERROR = Message.error(
			"shop.message.error", new Object[0]);
	protected static final Message SHOP_MESSAGE_SUCCESS = Message.success(
			"shop.message.success", new Object[0]);
	private static final String CONSTRAINT_VIOLATIONS = "constraintViolations";

	@Resource(name = "validator")
	private Validator validator;

	@InitBinder
	protected void initBinder(WebDataBinder paramWebDataBinder) {
		paramWebDataBinder.registerCustomEditor(String.class,
				new HtmlCleanEditor(true, true));
		paramWebDataBinder.registerCustomEditor(Date.class,
				new DateEditor(true));
	}

	protected boolean validate(Object paramObject, Class<?>[] paramArrayOfClass) {
		Set<?> set = this.validator.validate(paramObject, paramArrayOfClass);
		if (set.isEmpty()) {
			return true;
		}

		RequestAttributes requestAttributes = RequestContextHolder
				.currentRequestAttributes();
		requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS, set, 0);

		return false;
	}

	protected boolean validate(Class<?> paramClass, String paramString,
			Object paramObject, Class<?>[] paramArrayOfClass) {
		Set<?> set = this.validator.validateValue(paramClass, paramString,
				paramObject, paramArrayOfClass);
		if (set.isEmpty()) {
			return true;
		}

		RequestAttributes requestAttributes = RequestContextHolder
				.currentRequestAttributes();
		requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS, set, 0);

		return false;
	}

	protected String setScale(BigDecimal paramBigDecimal,
			boolean paramBoolean1, boolean paramBoolean2) {
		Setting setting = SettingUtils.get();
		String str = setting.setScale(paramBigDecimal).toString();
		if (paramBoolean1) {
			str = setting.getCurrencySign() + str;
		}

		if (paramBoolean2) {
			str = str + setting.getCurrencyUnit();
		}

		return str;
	}

	protected String getMessage(String paramString, Object[] paramArrayOfObject) {
		return SpringUtils.getMessage(paramString, paramArrayOfObject);
	}

	protected void setRedirect(RedirectAttributes paramRedirectAttributes,
			Message paramMessage) {
		if ((paramRedirectAttributes != null) && (paramMessage != null)) {
			paramRedirectAttributes.addFlashAttribute(
					FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME,
					paramMessage);
		}
	}
}