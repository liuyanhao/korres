package com.korres.service.impl;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.ProductNotifyDao;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.entity.ProductNotify;
import com.korres.service.MailService;
import com.korres.service.ProductNotifyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Page;
import com.korres.Pageable;

@Service("productNotifyServiceImpl")
public class ProductNotifyServiceImpl extends
		BaseServiceImpl<ProductNotify, Long> implements ProductNotifyService {

	@Resource(name = "productNotifyDaoImpl")
	private ProductNotifyDao productNotifyDao;

	@Resource(name = "mailServiceImpl")
	private MailService mailService;

	@Resource(name = "productNotifyDaoImpl")
	public void setBaseDao(ProductNotifyDao productNotifyDao) {
		super.setBaseDao(productNotifyDao);
	}

	@Transactional(readOnly = true)
	public boolean exists(Product product, String email) {
		return this.productNotifyDao.exists(product, email);
	}

	@Transactional(readOnly = true)
	public Page<ProductNotify> findPage(Member member, Boolean isMarketable,
			Boolean isOutOfStock, Boolean hasSent, Pageable pageable) {
		return this.productNotifyDao.findPage(member, isMarketable,
				isOutOfStock, hasSent, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Member member, Boolean isMarketable,
			Boolean isOutOfStock, Boolean hasSent) {
		return this.productNotifyDao.count(member, isMarketable, isOutOfStock,
				hasSent);
	}

	public int send(Long[] ids) {
		List<ProductNotify> lip = findList(ids);
		Iterator<ProductNotify> iterator = lip.iterator();
		while (iterator.hasNext()) {
			ProductNotify productNotify = iterator.next();
			this.mailService.sendProductNotifyMail(productNotify);
			productNotify.setHasSent(Boolean.valueOf(true));
			this.productNotifyDao.merge(productNotify);
		}

		return lip.size();
	}
}