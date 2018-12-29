package com.korres;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;

/*
 * 类名：DateEditor.java
 * 功能说明：时间公共类
 * 创建日期：2018-11-09 下午01:59:33
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class DateEditor extends PropertyEditorSupport {
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private boolean emptyAsNull;
	private String dateFormat = DATE_PATTERN;

	public DateEditor(boolean emptyAsNull) {
		this.emptyAsNull = emptyAsNull;
	}

	public DateEditor(boolean emptyAsNull, String dateFormat) {
		this.emptyAsNull = emptyAsNull;
		this.dateFormat = dateFormat;
	}

	public String getAsText() {
		Date localDate = (Date) getValue();
		return localDate != null ? new SimpleDateFormat(this.dateFormat)
				.format(localDate) : "";
	}

	public void setAsText(String text) {
		if (text == null) {
			setValue(null);
		} else {
			String str = text.trim();
			if ((this.emptyAsNull) && ("".equals(str)))
				setValue(null);
			else
				try {
					setValue(DateUtils.parseDate(str,
							CommonAttributes.DATE_PATTERNS));
				} catch (ParseException localParseException) {
					setValue(null);
				}
		}
	}
}