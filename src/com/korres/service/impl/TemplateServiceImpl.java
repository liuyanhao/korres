package com.korres.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import com.korres.service.TemplateService;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import com.korres.CommonAttributes;
import com.korres.Template;
import com.korres.Template.TemplateType;

@Service("templateServiceImpl")
public class TemplateServiceImpl implements TemplateService,
		ServletContextAware {
	private ServletContext servletContext;

	@Value("${template.loader_path}")
	private String[] path;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Cacheable( { "template" })
	public List<Template> getAll() {
		try {
			File file = new ClassPathResource(CommonAttributes.KORRES_XML_PATH).getFile();
			Document document = new SAXReader().read(file);
			ArrayList<Template> lit = new ArrayList<Template>();
			List<Element> lie = document.selectNodes("/korres/template");
			Iterator<Element> localIterator = lie.iterator();
			while (localIterator.hasNext()) {
				Element element = localIterator.next();
				Template template = getTemplate(element);
				lit.add(template);
			}
			return lit;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Cacheable( { "template" })
	public List<Template> getList(TemplateType type) {
		if (type != null)
			try {
				File localFile = new ClassPathResource(CommonAttributes.KORRES_XML_PATH).getFile();
				Document document = new SAXReader().read(localFile);
				ArrayList<Template> lit = new ArrayList<Template>();
				List<Element> lie = document
						.selectNodes("/korres/template[@type='" + type + "']");
				Iterator<Element> iterator = lie.iterator();
				while (iterator.hasNext()) {
					Element element = iterator.next();
					Template template = getTemplate(element);
					lit.add(template);
				}
				return lit;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		return getAll();
	}

	@Cacheable( { "template" })
	public Template get(String id) {
		try {
			File file = new ClassPathResource(CommonAttributes.KORRES_XML_PATH).getFile();
			Document document = new SAXReader().read(file);
			Element element = (Element) document
					.selectSingleNode("/korres/template[@id='" + id + "']");
			Template template = getTemplate(element);
			return template;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String read(String id) {
		Template template = get(id);
		return read(template);
	}

	public String read(Template template) {
		String path = this.servletContext.getRealPath(this.path[0]
				+ template.getTemplatePath());
		File localFile = new File(path);
		String str = null;
		try {
			str = FileUtils.readFileToString(localFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	public void write(String id, String content) {
		Template template = get(id);
		write(template, content);
	}

	public void write(Template template, String content) {
		String path = this.servletContext.getRealPath(this.path[0]
				+ template.getTemplatePath());
		File file = new File(path);
		try {
			FileUtils.writeStringToFile(file, content, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Template getTemplate(Element paramElement) {
		String id = paramElement.attributeValue("id");
		String type = paramElement.attributeValue("type");
		String name = paramElement.attributeValue("name");
		String templatePath = paramElement.attributeValue("templatePath");
		String staticPath = paramElement.attributeValue("staticPath");
		String description = paramElement.attributeValue("description");
		Template template = new Template();
		template.setId(id);
		template.setType(TemplateType.valueOf(type));
		template.setName(name);
		template.setTemplatePath(templatePath);
		template.setStaticPath(staticPath);
		template.setDescription(description);
		
		return template;
	}
}