package com.korres.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.korres.FileInfo;
import com.korres.FileInfo.FileInfoFileType;
import com.korres.FileInfo.FileInfoOrderType;

public abstract interface FileService {
	public abstract boolean isValid(FileInfoFileType paramFileType,
			MultipartFile paramMultipartFile);

	public abstract String upload(FileInfoFileType paramFileType,
			MultipartFile paramMultipartFile, boolean paramBoolean);

	public abstract String upload(FileInfoFileType paramFileType,
			MultipartFile paramMultipartFile);

	public abstract String uploadLocal(FileInfoFileType paramFileType,
			MultipartFile paramMultipartFile);

	public abstract List<FileInfo> browser(String paramString,
			FileInfoFileType paramFileType, FileInfoOrderType paramOrderType);
}