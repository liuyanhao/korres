package com.korres.service.impl;

import java.util.List;
import javax.annotation.Resource;
import com.korres.dao.ConsultationDao;
import com.korres.entity.Consultation;
import com.korres.entity.Member;
import com.korres.entity.Product;
import com.korres.service.ConsultationService;
import com.korres.service.StaticService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.korres.Filter;
import com.korres.Order;
import com.korres.Page;
import com.korres.Pageable;

/*
 * 类名：ConsultationServiceImpl.java
 * 功能说明：商品咨询service实现类
 * 创建日期：2013-12-20 下午05:01:41
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Service("consultationServiceImpl")
public class ConsultationServiceImpl extends
		BaseServiceImpl<Consultation, Long> implements ConsultationService {

	@Resource(name = "consultationDaoImpl")
	private ConsultationDao consultationDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "consultationDaoImpl")
	public void setBaseDao(ConsultationDao consultationDao) {
		super.setBaseDao(consultationDao);
	}

	@Transactional(readOnly = true)
	public List<Consultation> findList(Member member, Product product,
			Boolean isShow, Integer count, List<Filter> filters,
			List<Order> orders) {
		return this.consultationDao.findList(member, product, isShow, count,
				filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "consultation" })
	public List<Consultation> findList(Member member, Product product,
			Boolean isShow, Integer count, List<Filter> filters,
			List<Order> orders, String cacheRegion) {
		return this.consultationDao.findList(member, product, isShow, count,
				filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<Consultation> findPage(Member member, Product product,
			Boolean isShow, Pageable pageable) {
		return this.consultationDao.findPage(member, product, isShow, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Member member, Product product, Boolean isShow) {
		return this.consultationDao.count(member, product, isShow);
	}

	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void reply(Consultation consultation, Consultation replyConsultation) {
		if ((consultation == null) || (replyConsultation == null))
			return;
		consultation.setIsShow(Boolean.valueOf(true));
		this.consultationDao.merge(consultation);
		replyConsultation.setIsShow(Boolean.valueOf(true));
		replyConsultation.setProduct(consultation.getProduct());
		replyConsultation.setForConsultation(consultation);
		this.consultationDao.persist(replyConsultation);
		Product product = consultation.getProduct();
		if (product != null) {
			this.consultationDao.flush();
			this.staticService.build(product);
		}
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void save(Consultation consultation) {
		super.save(consultation);
		Product product = consultation.getProduct();
		if (product != null) {
			this.consultationDao.flush();
			this.staticService.build(product);
		}
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public Consultation update(Consultation consultation) {
		Consultation localConsultation = (Consultation) super
				.update(consultation);
		Product product = localConsultation.getProduct();
		if (product != null) {
			this.consultationDao.flush();
			this.staticService.build(product);
		}
		return localConsultation;
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public Consultation update(Consultation consultation,
			String[] ignoreProperties) {
		return (Consultation) super.update(consultation, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void delete(Long[] ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review",
			"consultation" }, allEntries = true)
	public void delete(Consultation consultation) {
		if (consultation != null) {
			super.delete(consultation);
			Product product = consultation.getProduct();
			if (product != null) {
				this.consultationDao.flush();
				this.staticService.build(product);
			}
		}
	}
}