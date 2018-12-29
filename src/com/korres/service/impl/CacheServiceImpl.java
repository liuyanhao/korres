package com.korres.service.impl;

import javax.annotation.Resource;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import com.korres.service.CacheService;
import com.korres.util.SettingUtils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.TemplateModelException;

@Service("cacheServiceImpl")
public class CacheServiceImpl implements CacheService {

	@Resource(name = "ehCacheManager")
	private CacheManager cacheManager;

	@Resource(name = "messageSource")
	private ReloadableResourceBundleMessageSource messageSource;

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	public String getDiskStorePath() {
		return this.cacheManager.getConfiguration().getDiskStoreConfiguration()
				.getPath();
	}

	public int getCacheSize() {
		int i = 0;
		String[] arrayOfString1 = this.cacheManager.getCacheNames();
		if (arrayOfString1 != null)
			for (String str : arrayOfString1) {
				Ehcache localEhcache = this.cacheManager.getEhcache(str);
				if (localEhcache != null)
					i += localEhcache.getSize();
			}
		return i;
	}

	@CacheEvict(value = { "setting", "authorization", "logConfig", "template",
			"shipping", "area", "seo", "adPosition", "memberAttribute",
			"navigation", "tag", "friendLink", "brand", "article",
			"articleCategory", "product", "productCategory", "review",
			"consultation", "promotion" }, allEntries = true)
	public void clear() {
		this.messageSource.clearCache();
		try {
			this.freeMarkerConfigurer.getConfiguration().setSharedVariable(
					"setting", SettingUtils.get());
		} catch (TemplateModelException localTemplateModelException) {
			localTemplateModelException.printStackTrace();
		}
		this.freeMarkerConfigurer.getConfiguration().clearTemplateCache();
	}
}