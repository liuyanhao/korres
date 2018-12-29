package com.korres.service.impl;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;

import com.korres.util.SettingUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.korres.Setting;

@Service("captchaServiceImpl")
public class CaptchaServiceImpl
  implements com.korres.service.CaptchaService
{

  @Resource(name="imageCaptchaService")
  private com.octo.captcha.service.CaptchaService captchaService;

  public BufferedImage buildImage(String captchaId)
  {
    return (BufferedImage)this.captchaService.getChallengeForID(captchaId);
  }

  public boolean isValid(Setting.CaptchaType captchaType, String captchaId, String captcha)
  {
    Setting localSetting = SettingUtils.get();
    if ((captchaType == null) || (ArrayUtils.contains(localSetting.getCaptchaTypes(), captchaType)))
    {
      if ((StringUtils.isNotEmpty(captchaId)) && (StringUtils.isNotEmpty(captcha)))
        try
        {
          return this.captchaService.validateResponseForID(captchaId, captcha.toUpperCase()).booleanValue();
        }
        catch (Exception e)
        {
          return false;
        }
      return false;
    }
    return true;
  }
}