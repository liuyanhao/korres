package com.korres.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.korres.plugin.PaymentPlugin;
import com.korres.plugin.StoragePlugin;
import com.korres.service.PluginService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.Predicate;

@Service("pluginServiceImpl")
public class PluginServiceImpl implements PluginService {

	@Resource
	private List<PaymentPlugin> paymentPlugins = new ArrayList<PaymentPlugin>();

	@Resource
	private List<StoragePlugin> storagePlugins = new ArrayList<StoragePlugin>();

	@Resource
	private Map<String, PaymentPlugin> paymentPluginMap = new HashMap<String, PaymentPlugin>();

	@Resource
	private Map<String, StoragePlugin> storagePluginMap = new HashMap<String, StoragePlugin>();

	public List<PaymentPlugin> getPaymentPlugins() {
		Collections.sort(this.paymentPlugins);
		return this.paymentPlugins;
	}

	public List<StoragePlugin> getStoragePlugins() {
		Collections.sort(this.storagePlugins);
		return this.storagePlugins;
	}

	public List<PaymentPlugin> getPaymentPlugins(final boolean isEnabled) {
		List<PaymentPlugin> lipp = new ArrayList<PaymentPlugin>();
		// CollectionUtils.select(this.IIIllIlI, new PluginServiceImpl$1(this,
		// isEnabled), localArrayList);

		CollectionUtils.select(this.paymentPlugins, new Predicate() {
			public boolean evaluate(Object object) {
				PaymentPlugin paymentPlugin = (PaymentPlugin) object;
				return paymentPlugin.getIsEnabled() == isEnabled;
			}
		}, lipp);

		Collections.sort(lipp);

		return lipp;
	}

	public List<StoragePlugin> getStoragePlugins(final boolean isEnabled) {
		List<StoragePlugin> lisp = new ArrayList<StoragePlugin>();
		// CollectionUtils.select(this.IIIllIll, new PluginServiceImpl$2(this,
		// isEnabled), localArrayList);

		CollectionUtils.select(this.storagePlugins, new Predicate() {
			public boolean evaluate(Object object) {
				StoragePlugin storagePlugin = (StoragePlugin) object;
				return storagePlugin.getIsEnabled() == isEnabled;
			}
		}, lisp);

		Collections.sort(lisp);

		return lisp;
	}

	public PaymentPlugin getPaymentPlugin(String id) {
		return (PaymentPlugin) this.paymentPluginMap.get(id);
	}

	public StoragePlugin getStoragePlugin(String id) {
		return (StoragePlugin) this.storagePluginMap.get(id);
	}
}