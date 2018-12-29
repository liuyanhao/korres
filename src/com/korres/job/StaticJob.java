package com.korres.job;

import javax.annotation.Resource;
import com.korres.service.StaticService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 * 类名：StaticJob.java
 * 功能说明：定时初始化所有静态页面
 * 创建日期：2013-8-9 下午01:41:25
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Component("staticJob")
@Lazy(false)
public class StaticJob {

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Scheduled(cron = "${job.static_build.cron}")
	public void build() {
		this.staticService.buildAll();
	}
}