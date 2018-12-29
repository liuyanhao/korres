package com.korres.template.method;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import java.math.BigDecimal;
import java.util.List;
import com.korres.util.SettingUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.korres.Setting;

@Component("currencyMethod")
public class CurrencyMethod
  implements TemplateMethodModel
{
  public SimpleScalar exec(List arguments)
  {
    if ((arguments != null) && (!arguments.isEmpty()) && (arguments.get(0) != null) && (StringUtils.isNotEmpty(arguments.get(0).toString())))
    {
      boolean bool1 = false;
      boolean bool2 = false;
      if (arguments.size() == 2)
      {
        if (arguments.get(1) != null)
          bool1 = Boolean.valueOf(arguments.get(1).toString()).booleanValue();
      }
      else if (arguments.size() > 2)
      {
        if (arguments.get(1) != null)
          bool1 = Boolean.valueOf(arguments.get(1).toString()).booleanValue();
        if (arguments.get(2) != null)
          bool2 = Boolean.valueOf(arguments.get(2).toString()).booleanValue();
      }
      Setting localSetting = SettingUtils.get();
      BigDecimal localBigDecimal = new BigDecimal(arguments.get(0).toString());
      String str = localSetting.setScale(localBigDecimal).toString();
      if (bool1)
        str = localSetting.getCurrencySign() + str;
      if (bool2)
        str = str + localSetting.getCurrencyUnit();
      return new SimpleScalar(str);
    }
    return null;
  }
}