package com.korres.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.korres.service.LogConfigService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.korres.CommonAttributes;
import com.korres.LogConfig;

@Service("logConfigServiceImpl")
public class LogConfigServiceImpl implements LogConfigService {
	@Cacheable( { "logConfig" })
	public List<LogConfig> getAll() {
		try {
			File file = new ClassPathResource(CommonAttributes.KORRES_XML_PATH).getFile();
			Document localDocument = new SAXReader().read(file);
			List<Element> list = localDocument.selectNodes("/korres/logConfig");
			List<LogConfig> lilc = new ArrayList<LogConfig>();
			Iterator<Element> iterator = list.iterator();
			while (iterator.hasNext()) {
				Element element = iterator.next();
				String operation = element.attributeValue("operation");
				String urlPattern = element.attributeValue("urlPattern");
				LogConfig logConfig = new LogConfig();
				logConfig.setOperation(operation);
				logConfig.setUrlPattern(urlPattern);
				lilc.add(logConfig);
			}

			return lilc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}