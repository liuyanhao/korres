package com.korres.service;

import java.awt.image.BufferedImage;

import com.korres.Setting.CaptchaType;

public abstract interface CaptchaService {
	public abstract BufferedImage buildImage(String paramString);

	public abstract boolean isValid(CaptchaType paramCaptchaType,
			String paramString1, String paramString2);
}