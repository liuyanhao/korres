package com.korres.controller.admin;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.MessagingException;

import com.korres.service.CacheService;
import com.korres.service.FileService;
import com.korres.service.MailService;
import com.korres.service.StaticService;
import com.korres.util.SettingUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.korres.CommonAttributes;
import com.korres.Message;
import com.korres.Setting;
import com.korres.FileInfo.FileInfoFileType;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPSenderFailedException;

@Controller("adminstingController")
@RequestMapping({ "/admin/setting" })
public class SettingController extends BaseController {

	@javax.annotation.Resource(name = "fileServiceImpl")
	private FileService fileService;

	@javax.annotation.Resource(name = "mailServiceImpl")
	private MailService mailService;

	@javax.annotation.Resource(name = "cacheServiceImpl")
	private CacheService cacheService;

	@javax.annotation.Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@RequestMapping(value = { "/mail_test" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message mailTest(String smtpFromMail, String smtpHost,
			Integer smtpPort, String smtpUsername, String smtpPassword,
			String toMail) {
		if (StringUtils.isEmpty(toMail)){
			return ADMIN_MESSAGE_ERROR;
		}
		
		Setting setting = SettingUtils.get();
		if (StringUtils.isEmpty(smtpPassword)){
			smtpPassword = setting.getSmtpPassword();
		}
		
		try {
			if ((!validator(Setting.class, "smtpFromMail", smtpFromMail,
					new Class[0]))
					|| (!validator(Setting.class, "smtpHost", smtpHost,
							new Class[0]))
					|| (!validator(Setting.class, "smtpPort", smtpPort,
							new Class[0]))
					|| (!validator(Setting.class, "smtpUsername", smtpUsername,
							new Class[0]))){
				return ADMIN_MESSAGE_ERROR;
			}
			
			this.mailService.sendTestMail(smtpFromMail, smtpHost, smtpPort,
					smtpUsername, smtpPassword, toMail);
		} catch (MailSendException mailSendException) {
			Exception[] arrayOfException1 = mailSendException
					.getMessageExceptions();
			if (arrayOfException1 != null)
				for (Exception e : arrayOfException1) {
					if ((e instanceof SMTPSendFailedException)) {
						SMTPSendFailedException se = (SMTPSendFailedException) e;
						Exception exception = se.getNextException();
						if (exception instanceof SMTPSenderFailedException){
							return Message.error(
									"admin.setting.mailTestSenderFailed",
									new Object[0]);
						}
					} else if (e instanceof MessagingException) {
						MessagingException me = (MessagingException) e;
						Exception exception = me.getNextException();
						if (exception instanceof UnknownHostException){
							return Message.error(
									"admin.setting.mailTestUnknownHost",
									new Object[0]);
						}
						
						if (exception instanceof ConnectException){
							return Message.error(
									"admin.setting.mailTestConnect",
									new Object[0]);
						}
					}
				}
			
			return Message.error("admin.setting.mailTestError", new Object[0]);
		} catch (MailAuthenticationException localMailAuthenticationException) {
			return Message.error("admin.setting.mailTestAuthentication",
					new Object[0]);
		} catch (Exception e) {
			return Message.error("admin.setting.mailTestError", new Object[0]);
		}
		
		return Message.success("admin.setting.mailTestSuccess", new Object[0]);
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(ModelMap model) {
		model.addAttribute("watermarkPositions",
				Setting.WatermarkPosition.values());
		model.addAttribute("roundTypes", Setting.RoundType.values());
		model.addAttribute("captchaTypes", Setting.CaptchaType.values());
		model.addAttribute("accountLockTypes", Setting.AccountLockType.values());
		model.addAttribute("stockAllocationTimes",
				Setting.StockAllocationTime.values());
		model.addAttribute("reviewAuthorities",
				Setting.ReviewAuthority.values());
		model.addAttribute("consultationAuthorities",
				Setting.ConsultationAuthority.values());
		
		return "/admin/setting/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Setting setting, MultipartFile watermarkImageFile,
			RedirectAttributes redirectAttributes) {
		if (!validator(setting, new Class[0])){
			return "/admin/common/error";
		}
		
		if ((setting.getUsernameMinLength().intValue() > setting
				.getUsernameMaxLength().intValue())
				|| (setting.getPasswordMinLength().intValue() > setting
						.getPasswordMinLength().intValue())){
			return "/admin/common/error";
		}
		
		Setting localSetting = SettingUtils.get();
		if (StringUtils.isEmpty(setting.getSmtpPassword()))
			setting.setSmtpPassword(localSetting.getSmtpPassword());
		if ((watermarkImageFile != null) && (!watermarkImageFile.isEmpty())) {
			if (!this.fileService.isValid(FileInfoFileType.image,
					watermarkImageFile)) {
				setRedirectAttributes(redirectAttributes,
						Message.error("admin.upload.invalid", new Object[0]));
				return "redirect:edit.jhtml";
			}
			String localObject1 = this.fileService.uploadLocal(
					FileInfoFileType.image, watermarkImageFile);
			setting.setWatermarkImage(localObject1);
		} else {
			setting.setWatermarkImage(localSetting.getWatermarkImage());
		}
		setting.setCnzzSiteId(localSetting.getCnzzSiteId());
		setting.setCnzzPassword(localSetting.getCnzzPassword());
		SettingUtils.set(setting);
		this.cacheService.clear();
		this.staticService.buildIndex();
		this.staticService.buildOther();
		OutputStream out = null;
		label416: try {
			ClassPathResource classPathResource = new ClassPathResource(CommonAttributes.KORRES_PROPERTIES_PATH);
			Properties properties = PropertiesLoaderUtils.loadProperties(classPathResource);
			String delay = properties.getProperty("template.update_delay");
			String seconds = properties.getProperty("message.cache_seconds");
			if (setting.getIsDevelopmentEnabled().booleanValue()) {
				if ((!delay.equals("0")) || (!seconds.equals("0"))) {
					out = new FileOutputStream(classPathResource.getFile());
					properties.setProperty("template.update_delay", "0");
					properties.setProperty("message.cache_seconds", "0");
					properties.store(out, "KORRES PROPERTIES");
					break label416;
				}
			} else if ((delay.equals("0")) || (seconds.equals("0"))) {
				out = new FileOutputStream(classPathResource.getFile());
				properties.setProperty("template.update_delay", "3600");
				properties.setProperty("message.cache_seconds", "3600");
				properties.store(out, "KORRES PROPERTIES");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
		
		setRedirectAttributes(redirectAttributes, ADMIN_MESSAGE_SUCCESS);
		
		return "redirect:edit.jhtml";
	}
}