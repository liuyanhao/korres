package com.korres.plugin.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.korres.entity.PluginConfig;
import com.korres.plugin.StoragePlugin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

import com.korres.FileInfo;

@Component("ftpPlugin")
public class FtpPlugin extends StoragePlugin {
	public String getName() {
		return "FTP存储";
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
		return "ftp/install.jhtml";
	}

	public String getUninstallUrl() {
		return "ftp/uninstall.jhtml";
	}

	public String getSettingUrl() {
		return "ftp/setting.jhtml";
	}

	public void upload(String path, File file, String contentType) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String host = pluginConfig.getAttribute("host");
			Integer port = Integer.valueOf(pluginConfig.getAttribute("port"));
			String username = pluginConfig.getAttribute("username");
			String password = pluginConfig.getAttribute("password");
			FTPClient ftp = new FTPClient();
			FileInputStream ins = null;
			try {
				ins = new FileInputStream(file);
				ftp.connect(host, port.intValue());
				ftp.login(username, password);
				ftp.setFileTransferMode(10);
				ftp.setFileType(2);
				ftp.enterLocalPassiveMode();
				if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					String str4 = StringUtils.substringBeforeLast(path, "/");
					String str5 = StringUtils.substringAfterLast(path, "/");
					if (!ftp.changeWorkingDirectory(str4)) {
						String[] arrayOfString1 = StringUtils.split(str4, "/");
						String str6 = "/";
						ftp.changeWorkingDirectory(str6);
						for (String str7 : arrayOfString1) {
							str6 = str6 + str7 + "/";
							if (!ftp.changeWorkingDirectory(str6)) {
								ftp.makeDirectory(str7);
								ftp.changeWorkingDirectory(str6);
							}
						}
					}

					ftp.storeFile(str5, ins);
					ftp.logout();
				}
			} catch (IOException e) {
				e.printStackTrace();
				IOUtils.closeQuietly(ins);
				if (ftp.isConnected())
					try {
						ftp.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			} finally {
				IOUtils.closeQuietly(ins);
				if (ftp.isConnected())
					try {
						ftp.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	public String getUrl(String path) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String str = pluginConfig.getAttribute("urlPrefix");
			return str + path;
		}
		return null;
	}

	public List<FileInfo> browser(String path) {
		List<FileInfo> liif = new ArrayList<FileInfo>();
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String host = pluginConfig.getAttribute("host");
			Integer port = Integer.valueOf(pluginConfig.getAttribute("port"));
			String username = pluginConfig.getAttribute("username");
			String password = pluginConfig.getAttribute("password");
			String urlPrefix = pluginConfig.getAttribute("urlPrefix");
			FTPClient ftp = new FTPClient();
			try {
				ftp.connect(host, port.intValue());
				ftp.login(username, password);
				ftp.setFileTransferMode(10);
				ftp.setFileType(2);
				ftp.enterLocalPassiveMode();
				if ((FTPReply.isPositiveCompletion(ftp.getReplyCode()))
						&& (ftp.changeWorkingDirectory(path))){
					for (FTPFile ftpFile : ftp.listFiles()) {
						FileInfo fileInfo = new FileInfo();
						fileInfo.setName(ftpFile.getName());
						fileInfo.setUrl(urlPrefix + path + ftpFile.getName());
						fileInfo.setIsDirectory(Boolean.valueOf(ftpFile
								.isDirectory()));
						fileInfo.setSize(Long.valueOf(ftpFile.getSize()));
						fileInfo.setLastModified(ftpFile.getTimestamp()
								.getTime());
						liif.add(fileInfo);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (ftp.isConnected())
					try {
						ftp.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			} finally {
				if (ftp.isConnected())
					try {
						ftp.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}

		return liif;
	}
}