package com.korres.service.impl;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.korres.service.RSAService;
import com.korres.util.RSAUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("rsaServiceImpl")
public class RSAServiceImpl implements RSAService {
	private static final String PRIVATE_KEY = "privateKey";

	@Transactional(readOnly = true)
	public RSAPublicKey generateKey(HttpServletRequest request) {
		Assert.notNull(request);
		KeyPair keyPair = RSAUtils.generateKeyPair();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
		HttpSession session = request.getSession();
		session.setAttribute(PRIVATE_KEY, rsaPrivateKey);

		return rsaPublicKey;
	}

	@Transactional(readOnly = true)
	public void removePrivateKey(HttpServletRequest request) {
		Assert.notNull(request);
		HttpSession session = request.getSession();
		session.removeAttribute(PRIVATE_KEY);
	}

	@Transactional(readOnly = true)
	public String decryptParameter(String name, HttpServletRequest request) {
		Assert.notNull(request);
		if (name != null) {
			HttpSession httpSession = request.getSession();
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) httpSession
					.getAttribute(PRIVATE_KEY);
			String str = request.getParameter(name);
			if ((rsaPrivateKey != null) && (StringUtils.isNotEmpty(str)))
				return RSAUtils.decrypt(rsaPrivateKey, str);
		}
		return null;
	}
}