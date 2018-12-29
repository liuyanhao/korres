package com.korres.plugin;

import java.math.BigDecimal;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.korres.entity.PluginConfig;
import com.korres.service.PluginConfigService;
import com.korres.util.SettingUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;

import com.korres.Setting;

public abstract class PaymentPlugin implements Comparable<PaymentPlugin> {
	public static final String PAYMENT_NAME_ATTRIBUTE_NAME = "paymentName";
	public static final String FEE_TYPE_ATTRIBUTE_NAME = "feeType";
	public static final String FEE_ATTRIBUTE_NAME = "fee";
	public static final String LOGO_ATTRIBUTE_NAME = "logo";
	public static final String DESCRIPTION_ATTRIBUTE_NAME = "description";

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

	public String getPaymentName() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? pluginConfig.getAttribute("paymentName")
				: null;
	}

	public PaymentPluginFeeType getFeeType() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? PaymentPluginFeeType.valueOf(pluginConfig
				.getAttribute("feeType")) : null;
	}

	public BigDecimal getFee() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? new BigDecimal(pluginConfig
				.getAttribute("fee")) : null;
	}

	public String getLogo() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? pluginConfig.getAttribute("logo") : null;
	}

	public String getDescription() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig != null ? pluginConfig.getAttribute("description")
				: null;
	}

	public abstract String getUrl();

	public abstract PaymentPluginMethod getMethod();

	public abstract Integer getTimeout();

	public abstract Map<String, String> getParameterMap(String paramString1,
			BigDecimal paramBigDecimal, String paramString2,
			HttpServletRequest paramHttpServletRequest);

	public abstract boolean verify(String paramString,
			HttpServletRequest paramHttpServletRequest);

	public abstract BigDecimal getAmount(String paramString,
			HttpServletRequest paramHttpServletRequest);

	public abstract String getNotifyContext(String paramString,
			HttpServletRequest paramHttpServletRequest);

	protected String getReturnUrl(String paramString) {
		Setting setting = SettingUtils.get();
		return setting.getSiteUrl() + "/payment/return/" + paramString
				+ ".jhtml";
	}

	protected String getNotifyUrl(String paramString) {
		Setting setting = SettingUtils.get();
		return setting.getSiteUrl() + "/payment/notify/" + paramString
				+ ".jhtml";
	}

	public final BigDecimal getFee(BigDecimal amount) {
		Setting setting = SettingUtils.get();
		BigDecimal amt;
		if (getFeeType() == PaymentPluginFeeType.scale) {
			amt = amount.multiply(getFee());
		} else {
			amt = getFee();
		}
		return setting.setScale(amt);
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this == obj)
			return true;
		PaymentPlugin paymentPlugin = (PaymentPlugin) obj;
		return new EqualsBuilder().append(getId(), paymentPlugin.getId())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
	}

	public int compareTo(PaymentPlugin paymentPlugin) {
		return new CompareToBuilder().append(getOrder(),
				paymentPlugin.getOrder())
				.append(getId(), paymentPlugin.getId()).toComparison();
	}

	public enum PaymentPluginFeeType {
		scale, fixed;
	}

	public enum PaymentPluginMethod {
		post, get;
	}
}