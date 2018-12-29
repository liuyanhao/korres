package com.korres.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.core.io.ClassPathResource;

import com.korres.CommonAttributes;
import com.korres.EnumConverter;
import com.korres.Setting;

/*
 * 类名：SettingUtils.java
 * 功能说明：读取常量工具类
 * 创建日期：2013-8-14 下午04:10:10
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public final class SettingUtils {
	private static final CacheManager cacheManager = CacheManager.create();
	private static final BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new ConvertUtilsBean(){
		public String convert(Object value)
		  {
		    if (value != null)
		    {
		      Class clazz = value.getClass();
		      if ((clazz.isEnum()) && (super.lookup(clazz) == null))
		      {
		        super.register(new EnumConverter(clazz), clazz);
		      }
		      else if ((clazz.isArray()) && (clazz.getComponentType().isEnum()))
		      {
		        if (super.lookup(clazz) == null)
		        {
		          ArrayConverter localObject = new ArrayConverter(clazz, new EnumConverter(clazz.getComponentType()), 0);
		          ((ArrayConverter)localObject).setOnlyFirstToString(false);
		          super.register((Converter)localObject, clazz);
		        }
		        Object localObject = super.lookup(clazz);
		        return (String)((Converter)localObject).convert(String.class, value);
		      }
		    }
		    return super.convert(value);
		  }

		  public Object convert(String value, Class clazz)
		  {
		    if ((clazz.isEnum()) && (super.lookup(clazz) == null))
		      super.register(new EnumConverter(clazz), clazz);
		    return super.convert(value, clazz);
		  }

		  public Object convert(String[] values, Class clazz)
		  {
		    if ((clazz.isArray()) && (clazz.getComponentType().isEnum()) && (super.lookup(clazz.getComponentType()) == null))
		      super.register(new EnumConverter(clazz.getComponentType()), clazz.getComponentType());
		    return super.convert(values, clazz);
		  }

		  public Object convert(Object value, Class targetType)
		  {
		    if (super.lookup(targetType) == null)
		      if (targetType.isEnum())
		      {
		        super.register(new EnumConverter(targetType), targetType);
		      }
		      else if ((targetType.isArray()) && (targetType.getComponentType().isEnum()))
		      {
		        ArrayConverter arrayConverter = new ArrayConverter(targetType, new EnumConverter(targetType.getComponentType()), 0);
		        arrayConverter.setOnlyFirstToString(false);
		        super.register(arrayConverter, targetType);
		      }
		    return super.convert(value, targetType);
		  }
    });

	static {
		DateConverter dateConverter = new DateConverter();
		dateConverter.setPatterns(CommonAttributes.DATE_PATTERNS);
		beanUtilsBean.getConvertUtils().register(dateConverter, Date.class);
	}

	public static Setting get() {
		Ehcache ehcache = cacheManager.getEhcache("setting");
		net.sf.ehcache.Element element = ehcache.get(Setting.CACHE_KEY);
		Setting setting;
		if (element != null) {
			setting = (Setting) element.getObjectValue();
		} else {
			setting = new Setting();
			try {
				File file = new ClassPathResource(CommonAttributes.KORRES_XML_PATH).getFile();
				Document document = new SAXReader().read(file);
				List<Object> list = document.selectNodes("/korres/setting");
				Iterator<Object> iterator = list.iterator();
				while (iterator.hasNext()) {
					org.dom4j.Element el = (org.dom4j.Element) iterator.next();
					String name = el.attributeValue("name");
					String value = el.attributeValue("value");
					try {
						beanUtilsBean.setProperty(setting, name, value);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ehcache.put(new net.sf.ehcache.Element(Setting.CACHE_KEY, setting));
		}
		return setting;
	}

	public static void set(Setting setting) {
		try {
			File file = new ClassPathResource(CommonAttributes.KORRES_XML_PATH).getFile();
			Document document = new SAXReader().read(file);
			List<Object> list = document.selectNodes("/korres/setting");
			Iterator<Object> iterator = list.iterator();
			while (iterator.hasNext()) {
				Element element = (org.dom4j.Element) iterator.next();
				try {
					String name = element.attributeValue("name");
					String value = beanUtilsBean.getProperty(setting, name);
					Attribute attribute = element.attribute("value");
					attribute.setValue(value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			OutputStream out = null;
			XMLWriter xmlWwriter = null;
			try {
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				format.setIndent(true);
				format.setIndent("\t");
				format.setNewlines(true);
				out = new FileOutputStream(file);
				xmlWwriter = new XMLWriter(out, format);
				xmlWwriter.write(document);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (xmlWwriter != null)
					try {
						xmlWwriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				IOUtils.closeQuietly(out);
			}
			Ehcache localEhcache = cacheManager.getEhcache("setting");
			localEhcache.put(new net.sf.ehcache.Element(Setting.CACHE_KEY,
					setting));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}