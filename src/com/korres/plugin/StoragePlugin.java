package com.korres.plugin;

import java.io.File;
import java.util.List;
import javax.annotation.Resource;
import com.korres.entity.PluginConfig;
import com.korres.service.PluginConfigService;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;

import com.korres.FileInfo;

public abstract class StoragePlugin implements Comparable<StoragePlugin> {

	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	public final String getId() {
		return ((Component) getClass().getAnnotation(Component.class)).value();
	}

	public abstract String getName();

	public abstract String getVersion();

	public abstract String getAuthor();

	public abstract String getSiteUrl();

	public abstract String getInstallUrl();

	public abstract String getUninstallUrl();

	public abstract String getSettingUrl();

	public boolean getIsInstalled() {
		return this.pluginConfigService.pluginIdExists(getId());
	}

	public PluginConfig getPluginConfig() {
		return this.pluginConfigService.findByPluginId(getId());
	}

	public boolean getIsEnabled() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? pluginConfig.getIsEnabled()
				.booleanValue() : false;
	}

	public String getAttribute(String name) {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? pluginConfig.getAttribute(name) : null;
	}

	public Integer getOrder() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? pluginConfig.getOrder() : null;
	}

	public abstract void upload(String paramString1, File paramFile,
			String paramString2);

	public abstract String getUrl(String paramString);

	public abstract List<FileInfo> browser(String paramString);

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		StoragePlugin storagePlugin = (StoragePlugin) obj;
		return new EqualsBuilder().append(getId(), storagePlugin.getId())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
	}

	public int compareTo(StoragePlugin storagePlugin) {
		return new CompareToBuilder().append(getOrder(),
				storagePlugin.getOrder())
				.append(getId(), storagePlugin.getId()).toComparison();
	}
}