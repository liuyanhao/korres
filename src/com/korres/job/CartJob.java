package com.korres.job;

import javax.annotation.Resource;
import com.korres.service.CartService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 * 类名：CartJob.java
 * 功能说明：定时更新购物车的过期时间
 * 创建日期：2013-8-9 下午01:42:55
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Component("cartJob")
@Lazy(false)
public class CartJob {

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@Scheduled(cron = "${job.cart_evict_expired.cron}")
	public void evictExpired() {
		this.cartService.evictExpired();
	}
}