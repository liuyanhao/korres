package com.korres.template.method;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;

@Component("abbreviateMethod")
public class AbbreviateMethod
  implements TemplateMethodModel
{
  private static final Pattern PATTERN = Pattern.compile("[\\u4e00-\\u9fa5\\ufe30-\\uffa0]+$");

  public SimpleScalar exec(List arguments)
  {
    if ((arguments != null) && (!arguments.isEmpty()) && (arguments.get(0) != null) && (StringUtils.isNotEmpty(arguments.get(0).toString())))
    {
      Integer maxLength = null;
      String str = null;
      if (arguments.size() == 2)
      {
        if (arguments.get(1) != null){
        	maxLength = Integer.valueOf(arguments.get(1).toString());
        }
      }
      else if (arguments.size() > 2)
      {
        if (arguments.get(1) != null){
        	maxLength = Integer.valueOf(arguments.get(1).toString());
        }
        
        if (arguments.get(2) != null){
          str = arguments.get(2).toString();
        }
      }
      
      return new SimpleScalar(abbreviate(arguments.get(0).toString(), maxLength, str));
    }
    
    return null;
  }

  private String abbreviate(String paramString1, Integer paramInteger, String paramString2)
  {
    if (paramInteger != null)
    {
      int i = 0;
      int j = 0;
      while (i < paramString1.length())
      {
        j = PATTERN.matcher(String.valueOf(paramString1.charAt(i))).find() ? j + 2 : j + 1;
        if (j >= paramInteger.intValue())
          break;
        i++;
      }
      if (i < paramString1.length())
      {
        if (paramString2 != null){
          return paramString1.substring(0, i + 1) + paramString2;
        }
        return paramString1.substring(0, i + 1);
      }
      
      return paramString1;
    }
    
    if (paramString2 != null){
      return paramString1 + paramString2;
    }
    
    return paramString1;
  }
}