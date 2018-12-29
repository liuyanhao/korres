package com.korres;

import java.beans.PropertyEditorSupport;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/*
 * 类名：HtmlCleanEditor.java
 * 功能说明：html 处理类
 * 创建日期：2018-12-14 下午04:02:57
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class HtmlCleanEditor extends PropertyEditorSupport {
	private boolean trim;
	private boolean emptyAsNull;
	private Whitelist whitelist = Whitelist.none();

	public HtmlCleanEditor(boolean trim, boolean emptyAsNull) {
		this.trim = trim;
		this.emptyAsNull = emptyAsNull;
	}

	public HtmlCleanEditor(boolean trim, boolean emptyAsNull,
			Whitelist whitelist) {
		this.trim = trim;
		this.emptyAsNull = emptyAsNull;
		this.whitelist = whitelist;
	}

	public String getAsText() {
		Object localObject = getValue();
		return localObject != null ? localObject.toString() : "";
	}

	public void setAsText(String text) {
		if (text != null) {
			String str = this.trim ? text.trim() : text;
			str = Jsoup.clean(str, this.whitelist);
			if ((this.emptyAsNull) && ("".equals(str)))
				str = null;
			setValue(str);
		} else {
			setValue(null);
		}
	}
}