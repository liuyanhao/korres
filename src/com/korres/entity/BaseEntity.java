package com.korres.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.groups.Default;

import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.korres.listener.EntityListener;

/*
 * 类名：BaseEntity.java
 * 功能说明：实体类基类
 * 创建日期：2018-08-20 下午04:36:46
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@EntityListeners( { EntityListener.class })
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -67188388306700736L;
	public static final String ID_PROPERTY_NAME = "id";
	public static final String CREATE_DATE_PROPERTY_NAME = "createDate";
	public static final String MODIFY_DATE_PROPERTY_NAME = "modifyDate";
	private Long id;
	private Date createDate;
	private Date modifyDate;

	@JsonProperty
	@DocumentId
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@DateBridge(resolution = Resolution.SECOND)
	@Column(nullable = false, updatable = false)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@JsonProperty
	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@DateBridge(resolution = Resolution.SECOND)
	@Column(nullable = false)
	public Date getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!BaseEntity.class.isAssignableFrom(obj.getClass()))
			return false;
		BaseEntity localBaseEntity = (BaseEntity) obj;
		return getId() != null ? getId().equals(localBaseEntity.getId())
				: false;
	}

	public int hashCode() {
		int i = 17;
		i += (getId() == null ? 0 : getId().hashCode() * 31);
		return i;
	}

	public abstract interface BaseEntitySave extends Default {

	}

	public abstract interface BaseEntityUpdate extends Default {
	}
}