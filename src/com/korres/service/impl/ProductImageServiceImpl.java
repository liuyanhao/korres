package com.korres.service.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import com.korres.entity.ProductImage;
import com.korres.plugin.StoragePlugin;
import com.korres.service.ProductImageService;
import com.korres.util.FreemarkerUtils;
import com.korres.util.ImageUtils;
import com.korres.util.SettingUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import com.korres.Setting;

@Service("productImageServiceImpl")
public class ProductImageServiceImpl implements ProductImageService,
		ServletContextAware {
	private static final String SUFFIX = "jpg";
	private static final String CONTENT_TYPE = "image/jpeg";
	private ServletContext servletContext;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	@Resource
	private List<StoragePlugin> storagePlugins;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private void build(final String source, final String large,
			final String medium, final String thumbnail, final File file,
			String contentType) {
		try {
			// this.taskExecutor.execute(new ProductImageServiceImpl$1(this,
			// paramFile, paramString1, paramString5, paramString2,
			// paramString3, paramString4));

			this.taskExecutor.execute(new Runnable() {
				public void run() {
					Collections.sort(storagePlugins);
					Iterator<StoragePlugin> iterator = storagePlugins
							.iterator();
					while (iterator.hasNext()) {
						StoragePlugin storagePlugin = iterator.next();
						if (storagePlugin.getIsEnabled()) {
							Setting setting = SettingUtils.get();
							String tmpdir = System
									.getProperty("java.io.tmpdir");
							File watermarkFile = new File(servletContext
									.getRealPath(setting.getWatermarkImage()));
							File largeFile = new File(tmpdir + "/upload_"
									+ UUID.randomUUID() + "." + SUFFIX);
							File mediumFile = new File(tmpdir + "/upload_"
									+ UUID.randomUUID() + "." + SUFFIX);
							File thumbnailFile = new File(tmpdir + "/upload_"
									+ UUID.randomUUID() + "." + SUFFIX);
							try {
								ImageUtils.zoom(file, largeFile,
										setting.getLargeProductImageWidth()
												.intValue(), setting
												.getLargeProductImageHeight()
												.intValue());
								ImageUtils.addWatermark(largeFile, largeFile,
										watermarkFile, setting
												.getWatermarkPosition(),
										setting.getWatermarkAlpha().intValue());
								ImageUtils.zoom(file, mediumFile, setting
										.getMediumProductImageWidth()
										.intValue(), setting
										.getMediumProductImageHeight()
										.intValue());
								ImageUtils.addWatermark(mediumFile, mediumFile,
										watermarkFile, setting
												.getWatermarkPosition(),
										setting.getWatermarkAlpha().intValue());
								ImageUtils.zoom(file, thumbnailFile, setting
										.getThumbnailProductImageWidth()
										.intValue(), setting
										.getThumbnailProductImageHeight()
										.intValue());
								storagePlugin.upload(source, file, large);
								storagePlugin.upload(large, largeFile,
										CONTENT_TYPE);
								storagePlugin.upload(medium, mediumFile,
										CONTENT_TYPE);
								storagePlugin.upload(thumbnail, thumbnailFile,
										CONTENT_TYPE);
							} finally {
								FileUtils.deleteQuietly(watermarkFile);
								FileUtils.deleteQuietly(largeFile);
								FileUtils.deleteQuietly(mediumFile);
								FileUtils.deleteQuietly(thumbnailFile);
							}
							break;
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void build(ProductImage productImage) {
		MultipartFile multipartFile = productImage.getFile();
		if ((multipartFile != null) && (!multipartFile.isEmpty()))
			try {
				Setting setting = SettingUtils.get();
				Map<String, String> model = new HashMap<String, String>();
				model.put("uuid", UUID.randomUUID().toString());
				String uploadPath = FreemarkerUtils.process(setting
						.getImageUploadPath(), model);
				String uuid = UUID.randomUUID().toString();
				String source = uploadPath
						+ uuid
						+ "-source."
						+ FilenameUtils.getExtension(multipartFile
								.getOriginalFilename());
				String large = uploadPath + uuid + "-large." + SUFFIX;
				String medium = uploadPath + uuid + "-medium." + SUFFIX;
				String thumbnail = uploadPath + uuid + "-thumbnail." + SUFFIX;
				Collections.sort(this.storagePlugins);
				Iterator<StoragePlugin> iterator = this.storagePlugins
						.iterator();
				while (iterator.hasNext()) {
					StoragePlugin storagePlugin = iterator.next();
					if (storagePlugin.getIsEnabled()) {
						File file = new File(System
								.getProperty("java.io.tmpdir")
								+ "/upload_" + UUID.randomUUID() + ".tmp");
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}

						multipartFile.transferTo(file);
						build(source, large, medium, thumbnail, file,
								multipartFile.getContentType());
						productImage.setSource(storagePlugin.getUrl(source));
						productImage.setLarge(storagePlugin.getUrl(large));
						productImage.setMedium(storagePlugin.getUrl(medium));
						productImage.setThumbnail(storagePlugin
								.getUrl(thumbnail));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}