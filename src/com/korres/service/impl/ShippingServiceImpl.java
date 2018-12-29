package com.korres.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korres.Setting;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.korres.dao.ShippingDao;
import com.korres.entity.Shipping;
import com.korres.service.ShippingService;
import com.korres.util.SettingUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("shippingServiceImpl")
public class ShippingServiceImpl extends BaseServiceImpl<Shipping, Long>
		implements ShippingService {

	@Resource(name = "shippingDaoImpl")
	private ShippingDao shippingDao;

	@Resource(name = "shippingDaoImpl")
	public void setBaseDao(ShippingDao shippingDao) {
		super.setBaseDao(shippingDao);
	}

	@Transactional(readOnly = true)
	public Shipping findBySn(String sn) {
		return this.shippingDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	@Cacheable( { "shipping" })
	public Map<String, Object> query(Shipping shipping) {
		Setting setting = SettingUtils.get();
		Map<String, Object> map = new HashMap<String, Object>();
		if ((shipping != null)
				&& (StringUtils.isNotEmpty(setting.getKuaidi100Key()))
				&& (StringUtils.isNotEmpty(shipping.getDeliveryCorpCode()))
				&& (StringUtils.isNotEmpty(shipping.getTrackingNo())))
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				URL url = new URL("http://api.kuaidi100.com/api?id="
						+ setting.getKuaidi100Key() + "&com="
						+ shipping.getDeliveryCorpCode() + "&nu="
						+ shipping.getTrackingNo() + "&show=0&muti=1&order=asc");
				map = (Map) objectMapper.readValue(url, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return map;
	}
}