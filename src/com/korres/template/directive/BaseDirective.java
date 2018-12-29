package com.korres.template.directive;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.korres.util.FreemarkerUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Order.OrderDirection;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public abstract class BaseDirective implements TemplateDirectiveModel {
	private static final String USE_CACHE = "useCache";
	private static final String CACHE_REGION = "cacheRegion";
	private static final String ID = "id";
	private static final String COUNT = "count";
	private static final String ORDERBY = "orderBy";
	private static final String IIIlllll = "\\s*,\\s*";
	private static final String IIlIIIII = "\\s+";

	protected boolean useCache(Environment paramEnvironment,
			Map<String, TemplateModel> paramMap) throws TemplateModelException {
		Boolean useCache = (Boolean) FreemarkerUtils.getParameter(USE_CACHE,
				Boolean.class, paramMap);
		return useCache != null ? useCache.booleanValue() : true;
	}

	protected String cacheRegion(Environment paramEnvironment,
			Map<String, TemplateModel> paramMap) throws TemplateModelException {
		String cacheRegion = (String) FreemarkerUtils.getParameter(
				CACHE_REGION, String.class, paramMap);
		return cacheRegion != null ? cacheRegion : paramEnvironment
				.getTemplate().getName();
	}

	protected Long getId(Map<String, TemplateModel> paramMap)
			throws TemplateModelException {
		return (Long) FreemarkerUtils.getParameter(ID, Long.class, paramMap);
	}

	protected Integer getCount(Map<String, TemplateModel> paramMap)
			throws TemplateModelException {
		return (Integer) FreemarkerUtils.getParameter(COUNT, Integer.class,
				paramMap);
	}

	protected List<Filter> getFilters(Map<String, TemplateModel> paramMap,
			Class<?> paramClass, String[] paramArrayOfString)
			throws TemplateModelException {
		List<Filter> lif = new ArrayList<Filter>();
		PropertyDescriptor[] propertyDescriptor = PropertyUtils
				.getPropertyDescriptors(paramClass);
		for (PropertyDescriptor pd : propertyDescriptor) {
			String name = pd.getName();
			Class clazz = pd.getPropertyType();
			if ((!ArrayUtils.contains(paramArrayOfString, name))
					&& (paramMap.containsKey(name))) {
				Object obj = FreemarkerUtils
						.getParameter(name, clazz, paramMap);
				lif.add(Filter.eq(name, obj));
			}
		}

		return lif;
	}

	protected List<Order> getOrders(Map<String, TemplateModel> paramMap,
			String[] paramArrayOfString) throws TemplateModelException {
		String str1 = StringUtils.trim((String) FreemarkerUtils.getParameter(
				ORDERBY, String.class, paramMap));
		List<Order> lio = new ArrayList<Order>();
		if (StringUtils.isNotEmpty(str1)) {
			String[] arrayOfString1 = str1.split("\\s*,\\s*");
			for (String str2 : arrayOfString1)
				if (StringUtils.isNotEmpty(str2)) {
					String localObject = null;
					OrderDirection direction = null;
					String[] arrayOfString3 = str2.split("\\s+");
					if (arrayOfString3.length == 1) {
						localObject = arrayOfString3[0];
					} else {
						if (arrayOfString3.length < 2) {
							continue;
						}

						localObject = arrayOfString3[0];
						try {
							direction = OrderDirection
									.valueOf(arrayOfString3[1]);
						} catch (IllegalArgumentException e) {
							continue;
						}
					}
					if (!ArrayUtils.contains(paramArrayOfString, localObject))
						lio.add(new Order(localObject, direction));
				}
		}

		return lio;
	}

	protected void setVariables(String paramString, Object paramObject,
			Environment paramEnvironment,
			TemplateDirectiveBody paramTemplateDirectiveBody)
			throws TemplateException, IOException {
		TemplateModel templateModel = FreemarkerUtils.getVariable(paramString,
				paramEnvironment);
		FreemarkerUtils.setVariable(paramString, paramObject, paramEnvironment);
		paramTemplateDirectiveBody.render(paramEnvironment.getOut());
		FreemarkerUtils.setVariable(paramString, templateModel,
				paramEnvironment);
	}

	protected void setVariables(Map<String, Object> paramMap,
			Environment paramEnvironment,
			TemplateDirectiveBody paramTemplateDirectiveBody)
			throws TemplateException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iterator = paramMap.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			TemplateModel templateModel = FreemarkerUtils.getVariable(name,
					paramEnvironment);
			map.put(name, templateModel);
		}

		FreemarkerUtils.setVariables(paramMap, paramEnvironment);
		paramTemplateDirectiveBody.render(paramEnvironment.getOut());
		FreemarkerUtils.setVariables(map, paramEnvironment);
	}
}