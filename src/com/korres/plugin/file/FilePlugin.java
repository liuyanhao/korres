package com.korres.plugin.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import com.korres.plugin.StoragePlugin;
import com.korres.util.SettingUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.korres.FileInfo;
import com.korres.Setting;

@Component("filePlugin")
public class FilePlugin extends StoragePlugin implements ServletContextAware {
	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getName() {
		return "本地文件存储";
	}

	public String getVersion() {
		return "1.0";
	}

	public String getAuthor() {
		return "KORRES";
	}

	public String getSiteUrl() {
		return "http://www.yanhaoit.top";
	}

	public String getInstallUrl() {
		return null;
	}

	public String getUninstallUrl() {
		return null;
	}

	public String getSettingUrl() {
		return "file/setting.jhtml";
	}

	public void upload(String path, File file, String contentType) {
		File f = new File(this.servletContext.getRealPath(path));
		try {
			FileUtils.moveFile(file, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUrl(String path) {
		Setting setting = SettingUtils.get();
		return setting.getSiteUrl() + path;
	}

	public List<FileInfo> browser(String path) {
		Setting setting = SettingUtils.get();
		List<FileInfo> lifi = new ArrayList<FileInfo>();
		File file = new File(this.servletContext.getRealPath(path));
		if ((file.exists()) && (file.isDirectory())){
			for (File f : file.listFiles()) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setName(f.getName());
				fileInfo.setUrl(setting.getSiteUrl() + path + f.getName());
				fileInfo.setIsDirectory(Boolean.valueOf(f.isDirectory()));
				fileInfo.setSize(Long.valueOf(f.length()));
				fileInfo.setLastModified(new Date(f.lastModified()));
				lifi.add(fileInfo);
			}
		}

		return lifi;
	}
}