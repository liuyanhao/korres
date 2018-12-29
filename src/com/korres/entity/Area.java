package com.korres.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Area.java
 * 功能说明：地区实体类
 * 创建日期：2018-08-20 下午02:55:45
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_area")
public class Area extends OrderEntity {
	private static final long serialVersionUID = -2158109459123036967L;
	private static final String SEPARATE = ",";
	private String name;
	private String fullName;
	private String treePath;
	private Area parent;
	private Set<Area> children = new HashSet<Area>();
	private Set<Member> members = new HashSet<Member>();
	private Set<Receiver> receivers = new HashSet<Receiver>();
	private Set<Order> orders = new HashSet<Order>();
	private Set<DeliveryCenter> deliveryCenters = new HashSet<DeliveryCenter>();

	@NotEmpty
	@Length(max = 100)
	@Column(nullable = false, length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false, length = 500)
	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(nullable = false, updatable = false)
	public String getTreePath() {
		return this.treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Area getParent() {
		return this.parent;
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("order asc")
	public Set<Area> getChildren() {
		return this.children;
	}

	public void setChildren(Set<Area> children) {
		this.children = children;
	}

	@OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
	public Set<Member> getMembers() {
		return this.members;
	}

	public void setMembers(Set<Member> members) {
		this.members = members;
	}

	@OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
	public Set<Receiver> getReceivers() {
		return this.receivers;
	}

	public void setReceivers(Set<Receiver> receivers) {
		this.receivers = receivers;
	}

	@OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
	public Set<DeliveryCenter> getDeliveryCenters() {
		return this.deliveryCenters;
	}

	public void setDeliveryCenters(Set<DeliveryCenter> deliveryCenters) {
		this.deliveryCenters = deliveryCenters;
	}

	@PrePersist
	public void prePersist() {
		Area area = getParent();
		if (area != null) {
			setFullName(area.getFullName() + getName());
			setTreePath(area.getTreePath() + area.getId() + SEPARATE);
		} else {
			setFullName(getName());
			setTreePath(SEPARATE);
		}
	}

	@PreUpdate
	public void preUpdate() {
		Area area = getParent();
		if (area != null) {
			setFullName(area.getFullName() + getName());
		} else {
			setFullName(getName());
		}
	}

	@PreRemove
	public void preRemove() {
		Set<Member> setMember = getMembers();
		if (setMember != null) {
			Iterator<Member> iterator = setMember.iterator();
			while (iterator.hasNext()) {
				Member member = iterator.next();
				member.setArea(null);
			}
		}

		Set<Receiver> setReceiver = getReceivers();
		if (setReceiver != null) {
			Iterator<Receiver> iterator = setReceiver.iterator();
			while (iterator.hasNext()) {
				Receiver receiver = iterator.next();
				receiver.setArea(null);
			}
		}

		Set<Order> setOrder = getOrders();
		if (setOrder != null) {
			Iterator<Order> iterator = setOrder.iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				order.setArea(null);
			}
		}

		Set<DeliveryCenter> setDeliveryCenter = getDeliveryCenters();
		if (setDeliveryCenter != null) {
			Iterator<DeliveryCenter> iterator = setDeliveryCenter.iterator();
			while (iterator.hasNext()) {
				DeliveryCenter deliveryCenter = iterator.next();
				deliveryCenter.setArea(null);
			}
		}
	}

	public String toString() {
		return getFullName();
	}
}