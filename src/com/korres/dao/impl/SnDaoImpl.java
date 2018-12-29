package com.korres.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import com.korres.dao.SnDao;
import com.korres.entity.Sn;
import com.korres.entity.Sn.SnType;
import com.korres.util.FreemarkerUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("snDaoImpl")
public class SnDaoImpl implements SnDao, InitializingBean {
	private HiloOptimizer product;
	private HiloOptimizer order;
	private HiloOptimizer payment;
	private HiloOptimizer refunds;
	private HiloOptimizer shipping;
	private HiloOptimizer returns;

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${sn.product.prefix}")
	private String productPrefix;

	@Value("${sn.product.maxLo}")
	private int productMaxLo;

	@Value("${sn.order.prefix}")
	private String orderPrefix;

	@Value("${sn.order.maxLo}")
	private int orderMaxLo;

	@Value("${sn.payment.prefix}")
	private String paymentPrefix;

	@Value("${sn.payment.maxLo}")
	private int paymentMaxLo;

	@Value("${sn.refunds.prefix}")
	private String refundsPrefix;

	@Value("${sn.refunds.maxLo}")
	private int refundsMaxLo;

	@Value("${sn.shipping.prefix}")
	private String shippingPrefix;

	@Value("${sn.shipping.maxLo}")
	private int shippingMaxLo;

	@Value("${sn.returns.prefix}")
	private String returnsPrefix;

	@Value("${sn.returns.maxLo}")
	private int returnsMaxLo;

	public void afterPropertiesSet() {
		this.product = new HiloOptimizer(this, SnType.product,
				this.productPrefix, this.productMaxLo);
		this.order = new HiloOptimizer(this, SnType.order, this.orderPrefix,
				this.orderMaxLo);
		this.payment = new HiloOptimizer(this, SnType.payment,
				this.paymentPrefix, this.paymentMaxLo);
		this.refunds = new HiloOptimizer(this, SnType.refunds,
				this.refundsPrefix, this.refundsMaxLo);
		this.shipping = new HiloOptimizer(this, SnType.shipping,
				this.shippingPrefix, this.shippingMaxLo);
		this.returns = new HiloOptimizer(this, SnType.returns,
				this.returnsPrefix, this.returnsMaxLo);
	}

	public String generate(SnType type) {
		Assert.notNull(type);
		if (type == SnType.product)
			return this.product.generate();
		if (type == SnType.order)
			return this.order.generate();
		if (type == SnType.payment)
			return this.payment.generate();
		if (type == SnType.refunds)
			return this.refunds.generate();
		if (type == SnType.shipping)
			return this.shipping.generate();
		if (type == SnType.returns)
			return this.returns.generate();
		return null;
	}

	private long IIIllIlI(SnType paramType) {
		String str = "select sn from Sn sn where sn.type = :type";
		Sn sn = (Sn) this.entityManager.createQuery(str, Sn.class)
				.setFlushMode(FlushModeType.COMMIT).setParameter("type",
						paramType).setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getSingleResult();
		long l = sn.getLastValue().longValue();
		sn.setLastValue(Long.valueOf(l + 1L));
		this.entityManager.merge(sn);
		return l;
	}

	class HiloOptimizer {
		private SnType type;
		private String prefix;
		private int maxLo;
		private int IIIllllI;
		private long IIIlllll;
		private long IIlIIIII;

		public HiloOptimizer(SnDaoImpl paramSnDaoImpl, SnType type,
				String prefix, int maxLo) {
			this.type = type;
			this.prefix = (prefix != null ? prefix.replace("{", "${") : "");
			this.maxLo = maxLo;
			this.IIIllllI = (maxLo + 1);
		}

		public synchronized String generate() {
			if (this.IIIllllI > this.maxLo) {
				this.IIlIIIII = IIIllIlI(this.type);
				this.IIIllllI = (this.IIlIIIII == 0L ? 1 : 0);
				this.IIIlllll = (this.IIlIIIII * (this.maxLo + 1));
			}
			return FreemarkerUtils.process(this.prefix, null)
					+ (this.IIIlllll + this.IIIllllI++);
		}
	}
}