package com.korres.controller.shop;

import java.awt.image.BufferedImage;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.korres.entity.Area;
import com.korres.service.AreaService;
import com.korres.service.CaptchaService;
import com.korres.service.RSAService;
import com.korres.util.SettingUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korres.Setting;

@Controller("shopCommonController")
@RequestMapping( { "/common" })
public class CommonController {

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@RequestMapping( { "/resource_not_found" })
	public String resourceNotFound() {
		return "/shop/common/resource_not_found";
	}

	@RequestMapping( { "/error" })
	public String error() {
		return "/shop/common/error";
	}

	@RequestMapping( { "/site_close" })
	public String siteClose() {
		Setting setting = SettingUtils.get();
		if (setting.getIsSiteEnabled().booleanValue()) {
			return "redirect:/";
		}

		return "/shop/common/site_close";
	}

	@RequestMapping(value = { "/public_key" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<String, String> publicKey(HttpServletRequest request) {
		RSAPublicKey rsaPublicKey = this.rsaService.generateKey(request);
		Map<String, String> map = new HashMap<String, String>();
		map.put("modulus", Base64.encodeBase64String(rsaPublicKey.getModulus()
				.toByteArray()));
		map.put("exponent", Base64.encodeBase64String(rsaPublicKey
				.getPublicExponent().toByteArray()));

		return map;
	}

	@RequestMapping(value = { "/area" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<Long, String> area(Long parentId) {
		List<Area> lia = null;
		Area area = this.areaService.find(parentId);
		if (area != null) {
			lia = new ArrayList<Area>(area.getChildren());
		} else {
			lia = this.areaService.findRoots();
		}

		Map<Long, String> map = new HashMap<Long, String>();
		Iterator<Area> iterator = lia.iterator();
		while (iterator.hasNext()) {
			Area ar = iterator.next();
			map.put(ar.getId(), ar.getName());
		}

		return map;
	}

	@RequestMapping(value = { "/captcha" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void image(String captchaId, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isEmpty(captchaId)) {
			captchaId = request.getSession().getId();
		}

		String str1 = new StringBuffer().append("yB").append("-").append("der")
				.append("ewoP").reverse().toString();
		String str2 = new StringBuffer().append("ten").append(".")
				.append("xxp").append("ohs").reverse().toString();
		response.addHeader(str1, str2);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0L);
		response.setContentType("image/jpeg");
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			BufferedImage localBufferedImage = this.captchaService
					.buildImage(captchaId);
			ImageIO.write(localBufferedImage, "jpg", out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}