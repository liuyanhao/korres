package com.korres.service.impl;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import com.korres.entity.ProductNotify;
import com.korres.entity.SafeKey;
import com.korres.service.MailService;
import com.korres.service.TemplateService;
import com.korres.util.SettingUtils;
import com.korres.util.SpringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.korres.Setting;

@Service("mailServiceImpl")
public class MailServiceImpl implements MailService {

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Resource(name = "javaMailSender")
	private JavaMailSenderImpl javaMailSenderImpl;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	@Resource(name = "templateServiceImpl")
	private TemplateService templateService;

	private void freeMarkerConfigurer(final MimeMessage paramMimeMessage) {
		try {
			// this.taskExecutor.execute(new MailServiceImpl$1(this,
			// paramMimeMessage));

			this.taskExecutor.execute(new Runnable() {

				public void run() {
					javaMailSenderImpl.send(paramMimeMessage);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String smtpFromMail, String smtpHost, Integer smtpPort,
			String smtpUsername, String smtpPassword, String toMail,
			String subject, String templatePath, Map<String, Object> model,
			boolean async) {
		Assert.hasText(smtpFromMail);
		Assert.hasText(smtpHost);
		Assert.notNull(smtpPort);
		Assert.hasText(smtpUsername);
		Assert.hasText(smtpPassword);
		Assert.hasText(toMail);
		Assert.hasText(subject);
		Assert.hasText(templatePath);
		try {
			Setting setting = SettingUtils.get();
			Configuration configuration = this.freeMarkerConfigurer
					.getConfiguration();
			freemarker.template.Template template = configuration
					.getTemplate(templatePath);
			String text = FreeMarkerTemplateUtils.processTemplateIntoString(
					template, model);
			this.javaMailSenderImpl.setHost(smtpHost);
			this.javaMailSenderImpl.setPort(smtpPort.intValue());
			this.javaMailSenderImpl.setUsername(smtpUsername);
			this.javaMailSenderImpl.setPassword(smtpPassword);
			MimeMessage mimeMessage = this.javaMailSenderImpl
					.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
					mimeMessage, false, "utf-8");
			mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting
					.getSiteName())
					+ " <" + smtpFromMail + ">");
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setTo(toMail);
			mimeMessageHelper.setText(text, true);
			if (async) {
				freeMarkerConfigurer(mimeMessage);
			} else {
				this.javaMailSenderImpl.send(mimeMessage);
			}
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void send(String toMail, String subject, String templatePath,
			Map<String, Object> model, boolean async) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting
				.getSmtpPort(), setting.getSmtpUsername(), setting
				.getSmtpPassword(), toMail, subject, templatePath, model, async);
	}

	public void send(String toMail, String subject, String templatePath,
			Map<String, Object> model) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting
				.getSmtpPort(), setting.getSmtpUsername(), setting
				.getSmtpPassword(), toMail, subject, templatePath, model, true);
	}

	public void send(String toMail, String subject, String templatePath) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting
				.getSmtpPort(), setting.getSmtpUsername(), setting
				.getSmtpPassword(), toMail, subject, templatePath, null, true);
	}

	public void sendTestMail(String smtpFromMail, String smtpHost,
			Integer smtpPort, String smtpUsername, String smtpPassword,
			String toMail) {
		Setting setting = SettingUtils.get();
		String subject = SpringUtils.getMessage(
				"admin.setting.testMailSubject", new Object[] { setting
						.getSiteName() });
		com.korres.Template template = this.templateService.get("testMail");
		send(smtpFromMail, smtpHost, smtpPort, smtpUsername, smtpPassword,
				toMail, subject, template.getTemplatePath(), null, false);
	}

	public void sendFindPasswordMail(String toMail, String username,
			SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = SpringUtils.getMessage("shop.password.mailSubject",
				new Object[] { setting.getSiteName() });
		com.korres.Template template = this.templateService
				.get("findPasswordMail");
		send(toMail, subject, template.getTemplatePath(), model);
	}

	public void sendProductNotifyMail(ProductNotify productNotify) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("productNotify", productNotify);
		String subject = SpringUtils.getMessage(
				"admin.productNotify.mailSubject", new Object[] { setting
						.getSiteName() });
		com.korres.Template template = this.templateService
				.get("productNotifyMail");
		send(productNotify.getEmail(), subject, template.getTemplatePath(),
				model);
	}
}