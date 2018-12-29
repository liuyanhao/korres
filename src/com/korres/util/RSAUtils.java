package com.korres.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.Assert;

/*
 * 类名：RSAUtils.java
 * 功能说明：RSA加密类
 * 创建日期：2013-8-14 下午04:10:52
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public final class RSAUtils {
	private static final Provider provider = new BouncyCastleProvider();
	private static final int KEY_SIZE = 1024;

	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
					"RSA", provider);
			keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());

			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encrypt(PublicKey publicKey, byte[] data) {
		Assert.notNull(publicKey);
		Assert.notNull(data);
		try {
			Cipher cipher = Cipher.getInstance("RSA", provider);
			cipher.init(1, publicKey);

			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encrypt(PublicKey publicKey, String text) {
		Assert.notNull(publicKey);
		Assert.notNull(text);
		byte[] arrayOfByte = encrypt(publicKey, text.getBytes());
		return arrayOfByte != null ? Base64.encodeBase64String(arrayOfByte)
				: null;
	}

	public static byte[] decrypt(PrivateKey privateKey, byte[] data) {
		Assert.notNull(privateKey);
		Assert.notNull(data);
		try {
			Cipher cipher = Cipher
					.getInstance("RSA/ECB/PKCS1Padding", provider);
			cipher.init(2, privateKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(PrivateKey privateKey, String text) {
		Assert.notNull(privateKey);
		Assert.notNull(text);
		byte[] arrayOfByte = decrypt(privateKey, Base64.decodeBase64(text));
		return arrayOfByte != null ? new String(arrayOfByte) : null;
	}
}