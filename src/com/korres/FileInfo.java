package com.korres;

import java.util.Date;

/*
 * 类名：FileInfo.java
 * 功能说明：文件公共类
 * 创建日期：2013-8-9 下午01:57:03
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class FileInfo {
	private String name;
	private String url;
	private Boolean isDirectory;
	private Long size;
	private Date lastModified;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getIsDirectory() {
		return this.isDirectory;
	}

	public void setIsDirectory(Boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public enum FileInfoFileType {
		image, flash, media, file;
	}

	public enum FileInfoOrderType {
		name, size, type;
	}
}