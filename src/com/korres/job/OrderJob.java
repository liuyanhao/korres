package com.korres.job;

import javax.annotation.Resource;
import com.korres.service.OrderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 * 类名：OrderJob.java
 * 功能说明：定时更新订单的状态
 * 创建日期：2013-8-9 下午01:43:44
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Component("orderJob")
@Lazy(false)
public class OrderJob {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Scheduled(cron = "${job.order_release_stock.cron}")
	public void releaseStock() {
		this.orderService.releaseStock();
	}
}