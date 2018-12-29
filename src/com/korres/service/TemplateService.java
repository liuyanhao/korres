package com.korres.service;

import java.util.List;

import com.korres.Template;
import com.korres.Template.TemplateType;


public abstract interface TemplateService {
	public abstract List<Template> getAll();

	public abstract List<Template> getList(TemplateType paramType);

	public abstract Template get(String paramString);

	public abstract String read(String paramString);

	public abstract String read(Template paramTemplate);

	public abstract void write(String paramString1, String paramString2);

	public abstract void write(Template paramTemplate, String paramString);
}