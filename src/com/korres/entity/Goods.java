package com.korres.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/*
 * 类名：Goods.java
 * 功能说明：产品实体类
 * 创建日期：2013-8-28 下午03:00:40
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_goods")
public class Goods extends BaseEntity {
	private static final long serialVersionUID = -6977025562650112419L;
	private Set<Product> products = new HashSet<Product>();

	@OneToMany(mappedBy = "goods", fetch = FetchType.EAGER, cascade = { javax.persistence.CascadeType.ALL }, orphanRemoval = true)
	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@Transient
	public Set<SpecificationValue> getSpecificationValues() {
		Set<SpecificationValue> set = new HashSet<SpecificationValue>();
		if (getProducts() != null) {
			Iterator<Product> iterator = getProducts().iterator();
			while (iterator.hasNext()) {
				Product product = iterator.next();
				set.addAll(product.getSpecificationValues());
			}
		}

		return set;
	}
}