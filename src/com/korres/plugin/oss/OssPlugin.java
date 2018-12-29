package com.korres.plugin.oss;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.korres.entity.PluginConfig;
import com.korres.plugin.StoragePlugin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.korres.FileInfo;

@Component("ossPlugin")
public class OssPlugin extends StoragePlugin {
	public String getName() {
		return "阿里云存储";
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
		return "oss/install.jhtml";
	}

	public String getUninstallUrl() {
		return "oss/uninstall.jhtml";
	}

	public String getSettingUrl() {
		return "oss/setting.jhtml";
	}

	public void upload(String path, File file, String contentType) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String accessId = pluginConfig.getAttribute("accessId");
			String accessKey = pluginConfig.getAttribute("accessKey");
			String bucketName = pluginConfig.getAttribute("bucketName");
			FileInputStream ins = null;
			try {
				ins = new FileInputStream(file);
				OSSClient client = new OSSClient(accessId, accessKey);
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentType(contentType);
				objectMetadata.setContentLength(file.length());
				client
						.putObject(bucketName, StringUtils.removeStart(path,
								"/"), ins, objectMetadata);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(ins);
			}
		}
	}

	public String getUrl(String path) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String url = pluginConfig.getAttribute("urlPrefix");
			return url + path;
		}
		return null;
	}

	public List<FileInfo> browser(String path) {
		List<FileInfo> lifi = new ArrayList<FileInfo>();
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String accessId = pluginConfig.getAttribute("accessId");
			String accessKey = pluginConfig.getAttribute("accessKey");
			String bucketName = pluginConfig.getAttribute("bucketName");
			String urlPrefix = pluginConfig.getAttribute("urlPrefix");
			try {
				OSSClient client = new OSSClient(accessId, accessKey);
				ListObjectsRequest listObjectsRequest = new ListObjectsRequest(
						bucketName);
				listObjectsRequest
						.setPrefix(StringUtils.removeStart(path, "/"));
				listObjectsRequest.setDelimiter("/");
				ObjectListing objectListing = client
						.listObjects(listObjectsRequest);
				Iterator<String> iterator = objectListing.getCommonPrefixes()
						.iterator();
				while (iterator.hasNext()) {
					String str = iterator.next();
					FileInfo fileInfo = new FileInfo();
					fileInfo.setName(StringUtils.substringAfterLast(StringUtils
							.removeEnd(str, "/"), "/"));
					fileInfo.setUrl(urlPrefix + "/" + str);
					fileInfo.setIsDirectory(Boolean.valueOf(true));
					fileInfo.setSize(Long.valueOf(0L));
					lifi.add(fileInfo);
				}

				Iterator<OSSObjectSummary> it = objectListing
						.getObjectSummaries().iterator();
				while (it.hasNext()) {
					OSSObjectSummary ossObjectSummary = it.next();
					if (!ossObjectSummary.getKey().endsWith("/")) {
						FileInfo fileInfo = new FileInfo();
						fileInfo.setName(StringUtils.substringAfterLast(
								ossObjectSummary.getKey(), "/"));
						fileInfo.setUrl(urlPrefix + "/"
								+ ossObjectSummary.getKey());
						fileInfo.setIsDirectory(Boolean.valueOf(false));
						fileInfo.setSize(Long.valueOf(ossObjectSummary
								.getSize()));
						fileInfo.setLastModified(ossObjectSummary
								.getLastModified());
						lifi.add(fileInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lifi;
	}
}