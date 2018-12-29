package com.korres;

import com.korres.util.SpringUtils;

/*
 * 类名：Message.java
 * 功能说明：消息公共类
 * 创建日期：2013-8-9 下午01:54:44
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class Message {
	private MessageType type;
	private String content;

	public Message() {
	}

	public Message(MessageType type, String content) {
		this.type = type;
		this.content = content;
	}

	public Message(MessageType type, String content, Object[] args) {
		this.type = type;
		this.content = SpringUtils.getMessage(content, args);
	}

	public static Message success(String content, Object[] args) {
		return new Message(MessageType.success, content, args);
	}

	public static Message warn(String content, Object[] args) {
		return new Message(MessageType.warn, content, args);
	}

	public static Message error(String content, Object[] args) {
		return new Message(MessageType.error, content, args);
	}

	public MessageType getType() {
		return this.type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString() {
		return SpringUtils.getMessage(this.content, new Object[0]);
	}

	public enum MessageType {
		success, warn, error;
	}
}