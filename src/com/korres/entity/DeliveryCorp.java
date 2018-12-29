package com.korres.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：DeliveryCorp.java
 * 功能说明：物流公司实体类
 * 创建日期：2013-8-28 下午02:56:57
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
@Entity
@Table(name = "xx_delivery_corp")
public class DeliveryCorp extends OrderEntity {
	private static final long serialVersionUID = 10595703086045998L;
	private String name;
	private String url;
	private String code;
	private Set<ShippingMethod> shippingMethods = new HashSet<ShippingMethod>();

	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(max = 200)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Length(max = 200)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@OneToMany(mappedBy = "defaultDeliveryCorp", fetch = FetchType.LAZY)
	public Set<ShippingMethod> getShippingMethods() {
		return this.shippingMethods;
	}

	public void setShippingMethods(Set<ShippingMethod> shippingMethods) {
		this.shippingMethods = shippingMethods;
	}

	@PreRemove
	public void preRemove() {
		Set<ShippingMethod> set = getShippingMethods();
		if (set != null) {
			Iterator<ShippingMethod> iterator = set.iterator();
			while (iterator.hasNext()) {
				ShippingMethod shippingMethod = iterator.next();
				shippingMethod.setDefaultDeliveryCorp(null);
			}
		}
	}
}