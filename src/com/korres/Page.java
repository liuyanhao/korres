package com.korres;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.korres.Order.OrderDirection;


/*
 * 类名：Page.java
 * 功能说明：分页泛型类
 * 创建日期：2013-8-9 下午01:53:40
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class Page<T> implements Serializable {
	private final List<T> content = new ArrayList();
	private final long total;
	private final Pageable pageable;

	public Page() {
		this.total = 0L;
		this.pageable = new Pageable();
	}

	public Page(List<T> content, long total, Pageable pageable) {
		this.content.addAll(content);
		this.total = total;
		this.pageable = pageable;
	}

	public int getPageNumber() {
		return this.pageable.getPageNumber();
	}

	public int getPageSize() {
		return this.pageable.getPageSize();
	}

	public String getSearchProperty() {
		return this.pageable.getSearchProperty();
	}

	public String getSearchValue() {
		return this.pageable.getSearchValue();
	}

	public String getOrderProperty() {
		return this.pageable.getOrderProperty();
	}

	public OrderDirection getOrderDirection() {
		return this.pageable.getOrderDirection();
	}

	public List<Order> getOrders() {
		return this.pageable.getOrders();
	}

	public List<Filter> getFilters() {
		return this.pageable.getFilters();
	}

	public int getTotalPages() {
		return (int) Math.ceil(getTotal() / getPageSize());
	}

	public List<T> getContent() {
		return this.content;
	}

	public long getTotal() {
		return this.total;
	}

	public Pageable getPageable() {
		return this.pageable;
	}
}