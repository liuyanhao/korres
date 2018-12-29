package com.korres.listener;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import com.korres.service.CacheService;
import com.korres.service.SearchService;
import com.korres.service.StaticService;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component("initListener")
public class InitListener implements
		ApplicationListener<ContextRefreshedEvent>, ServletContextAware {
	private static final String INSTALL_INIT_CONF = "/install_init.conf";
	private ServletContext servletContext;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "cacheServiceImpl")
	private CacheService cacheService;

	@Resource(name = "searchServiceImpl")
	private SearchService searchService;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if ((this.servletContext != null)
				&& (contextRefreshedEvent.getApplicationContext().getParent() == null)) {
			File file = new File(this.servletContext
					.getRealPath(INSTALL_INIT_CONF));
			if (file.exists()) {
				this.cacheService.clear();
				this.staticService.buildAll();
				this.searchService.purge();
				this.searchService.index();
				file.delete();
			} else {
				this.staticService.buildIndex();
				this.staticService.buildOther();
			}
		}
	}
}