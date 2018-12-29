package com.korres;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * 类名：Filter.java
 * 功能说明：条件过滤类
 * 创建日期：2018-11-09 下午01:56:04
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class Filter implements Serializable {
	private static final boolean IIIllIlI = false;
	private String property;
	private FilterOperator operator;
	private Object value;
	private Boolean ignoreCase = Boolean.valueOf(false);

	public Filter() {
	}

	public Filter(String property, FilterOperator operator, Object value) {
		this.property = property;
		this.operator = operator;
		this.value = value;
	}

	public Filter(String property, FilterOperator operator, Object value,
			boolean ignoreCase) {
		this.property = property;
		this.operator = operator;
		this.value = value;
		this.ignoreCase = Boolean.valueOf(ignoreCase);
	}

	public static Filter eq(String property, Object value) {
		return new Filter(property, FilterOperator.eq, value);
	}

	public static Filter eq(String property, Object value, boolean ignoreCase) {
		return new Filter(property, FilterOperator.eq, value, ignoreCase);
	}

	public static Filter ne(String property, Object value) {
		return new Filter(property, FilterOperator.ne, value);
	}

	public static Filter ne(String property, Object value, boolean ignoreCase) {
		return new Filter(property, FilterOperator.ne, value, ignoreCase);
	}

	public static Filter gt(String property, Object value) {
		return new Filter(property, FilterOperator.gt, value);
	}

	public static Filter lt(String property, Object value) {
		return new Filter(property, FilterOperator.lt, value);
	}

	public static Filter ge(String property, Object value) {
		return new Filter(property, FilterOperator.ge, value);
	}

	public static Filter le(String property, Object value) {
		return new Filter(property, FilterOperator.le, value);
	}

	public static Filter like(String property, Object value) {
		return new Filter(property, FilterOperator.like, value);
	}

	public static Filter in(String property, Object value) {
		return new Filter(property, FilterOperator.in, value);
	}

	public static Filter isNull(String property) {
		return new Filter(property, FilterOperator.isNull, null);
	}

	public static Filter isNotNull(String property) {
		return new Filter(property, FilterOperator.isNotNull, null);
	}

	public Filter ignoreCase() {
		this.ignoreCase = Boolean.valueOf(true);
		return this;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public FilterOperator getOperator() {
		return this.operator;
	}

	public void setOperator(FilterOperator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean getIgnoreCase() {
		return this.ignoreCase;
	}

	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this == obj)
			return true;
		Filter localFilter = (Filter) obj;
		return new EqualsBuilder()
				.append(getProperty(), localFilter.getProperty())
				.append(getOperator(), localFilter.getOperator())
				.append(getValue(), localFilter.getValue()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getProperty())
				.append(getOperator()).append(getValue()).toHashCode();
	}

	public enum FilterOperator {
		eq, ne, gt, lt, ge, le, like, in, isNull, isNotNull;

		public static FilterOperator fromString(String value) {
			return valueOf(value.toLowerCase());
		}
	}
}