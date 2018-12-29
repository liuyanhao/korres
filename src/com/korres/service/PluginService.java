package com.korres.service;

import java.util.List;
import com.korres.plugin.PaymentPlugin;
import com.korres.plugin.StoragePlugin;

public abstract interface PluginService {
	public abstract List<PaymentPlugin> getPaymentPlugins();

	public abstract List<StoragePlugin> getStoragePlugins();

	public abstract List<PaymentPlugin> getPaymentPlugins(boolean paramBoolean);

	public abstract List<StoragePlugin> getStoragePlugins(boolean paramBoolean);

	public abstract PaymentPlugin getPaymentPlugin(String paramString);

	public abstract StoragePlugin getStoragePlugin(String paramString);
}