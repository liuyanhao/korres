package com.korres.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * 类名：Message.java
 * 功能说明：消息实体类
 * 创建日期：2013-8-28 下午03:11:25
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
 */
@Entity
@Table(name = "xx_message")
public class Message extends BaseEntity {
	private static final long serialVersionUID = -5035343536762850722L;
	private String title;
	private String content;
	private String ip;
	private Boolean isDraft;
	private Boolean senderRead;
	private Boolean receiverRead;
	private Boolean senderDelete;
	private Boolean receiverDelete;
	private Member sender;
	private Member receiver;
	private Message forMessage;
	private Set<Message> replyMessages = new HashSet<Message>();

	@Column(nullable = false, updatable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotEmpty
	@Length(max = 1000)
	@Column(nullable = false, updatable = false, length = 1000)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(nullable = false, updatable = false)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(nullable = false, updatable = false)
	public Boolean getIsDraft() {
		return this.isDraft;
	}

	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}

	@Column(nullable = false)
	public Boolean getSenderRead() {
		return this.senderRead;
	}

	public void setSenderRead(Boolean senderRead) {
		this.senderRead = senderRead;
	}

	@Column(nullable = false)
	public Boolean getReceiverRead() {
		return this.receiverRead;
	}

	public void setReceiverRead(Boolean receiverRead) {
		this.receiverRead = receiverRead;
	}

	@Column(nullable = false)
	public Boolean getSenderDelete() {
		return this.senderDelete;
	}

	public void setSenderDelete(Boolean senderDelete) {
		this.senderDelete = senderDelete;
	}

	@Column(nullable = false)
	public Boolean getReceiverDelete() {
		return this.receiverDelete;
	}

	public void setReceiverDelete(Boolean receiverDelete) {
		this.receiverDelete = receiverDelete;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Member getSender() {
		return this.sender;
	}

	public void setSender(Member sender) {
		this.sender = sender;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Member getReceiver() {
		return this.receiver;
	}

	public void setReceiver(Member receiver) {
		this.receiver = receiver;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Message getForMessage() {
		return this.forMessage;
	}

	public void setForMessage(Message forMessage) {
		this.forMessage = forMessage;
	}

	@OneToMany(mappedBy = "forMessage", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("createDate asc")
	public Set<Message> getReplyMessages() {
		return this.replyMessages;
	}

	public void setReplyMessages(Set<Message> replyMessages) {
		this.replyMessages = replyMessages;
	}
}