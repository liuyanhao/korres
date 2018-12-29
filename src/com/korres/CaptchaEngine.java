package com.korres;

import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.awt.Color;
import java.awt.Font;
import org.springframework.core.io.ClassPathResource;

/*
 * 类名：CaptchaEngine.java
 * 功能说明：验证码图片引擎
 * 创建日期：2018-11-09 下午02:00:38
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class CaptchaEngine extends ListImageCaptchaEngine {
	private static final int IIIllIlI = 80;
	private static final int IIIllIll = 28;
	private static final int MIN_FONT_SIZE = 12;
	private static final int MAX_FONT_SIZE = 16;
	private static final int MIN_ACCEPTED_WORD_LENGTH = 4;
	private static final int MAX_ACCEPTED_WORD_LENGTH = 4;
	private static final String ACCEPTED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CLASS_PATH_RESOURCE = "/com/korres/captcha/";
	private static final Font[] fontsList = { new Font("nyala", 1, 16),
			new Font("Arial", 1, 16), new Font("nyala", 1, 16),
			new Font("Bell", 1, 16), new Font("Bell MT", 1, 16),
			new Font("Credit", 1, 16), new Font("valley", 1, 16),
			new Font("Impact", 1, 16) };
	private static final Color[] colorList = { new Color(255, 255, 255),
			new Color(255, 220, 220), new Color(220, 255, 255),
			new Color(220, 220, 255), new Color(255, 255, 220),
			new Color(220, 255, 220) };

	protected void buildInitialFactories() {
		RandomFontGenerator randomFontGenerator = new RandomFontGenerator(
				Integer.valueOf(MIN_FONT_SIZE), Integer.valueOf(MAX_FONT_SIZE),
				fontsList);
		FileReaderRandomBackgroundGenerator fileReaderRandomBackgroundGenerator = new FileReaderRandomBackgroundGenerator(
				Integer.valueOf(80), Integer.valueOf(28),
				new ClassPathResource(CLASS_PATH_RESOURCE).getPath());
		DecoratedRandomTextPaster decoratedRandomTextPaster = new DecoratedRandomTextPaster(
				Integer.valueOf(MIN_ACCEPTED_WORD_LENGTH), Integer
						.valueOf(MAX_ACCEPTED_WORD_LENGTH),
				new RandomListColorGenerator(colorList), new TextDecorator[0]);
		addFactory(new GimpyFactory(new RandomWordGenerator(ACCEPTED_CHARS),
				new ComposedWordToImage(randomFontGenerator,
						fileReaderRandomBackgroundGenerator,
						decoratedRandomTextPaster)));
	}
}