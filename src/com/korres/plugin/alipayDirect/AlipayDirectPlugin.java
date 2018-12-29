package com.korres.plugin.alipayDirect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.korres.plugin.PaymentPlugin;
import com.korres.util.MD5;

import org.springframework.stereotype.Component;

@Component("alipayDirectPlugin")
public class AlipayDirectPlugin extends PaymentPlugin {
	/**
     * 支付宝消息验证地址
     */
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
    
	public String getName() {
		return "支付宝即时交易";
	}

	public String getVersion() {
		return "1.0";
	}

	public String getAuthor() {
		return "KORRES";
	}

	public String getSiteUrl() {
		return "http://www.yanhaoit.top";
	}

	public String getInstallUrl() {
		return "alipay_direct/install.jhtml";
	}

	public String getUninstallUrl() {
		return "alipay_direct/uninstall.jhtml";
	}

	public String getSettingUrl() {
		return "alipay_direct/setting.jhtml";
	}

	public String getUrl() {
		return "https://mapi.alipay.com/gateway.do";
	}

	public PaymentPluginMethod getMethod() {
		return PaymentPluginMethod.get;
	}

	public Integer getTimeout() {
		return Integer.valueOf(21600);
	}
	
	/**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public String buildRequestMysign(Map<String, String> sPara) {
    	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = MD5.sign(prestr, getAttribute("key"), "utf-8");
       
        return mysign;
    }
	
	/**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sParaTemp);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", "MD5");

        return sPara;
    }

	public Map<String, String> getParameterMap(String sn, BigDecimal amount,
			String description, HttpServletRequest request) {
		//支付宝提供参数
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
        sParaTemp.put("partner", getAttribute("partner"));
        sParaTemp.put("_input_charset", "utf-8");
		sParaTemp.put("payment_type", "1");
		sParaTemp.put("notify_url", getNotifyUrl(sn));
		sParaTemp.put("return_url", getReturnUrl(sn));
//		sParaTemp.put("seller_email", "1094921525@qq.com");
		sParaTemp.put("seller_email", getAttribute("sellerEmail"));
		sParaTemp.put("out_trade_no", sn);
		sParaTemp.put("subject", description);
		DecimalFormat df = new DecimalFormat("#.00");
		sParaTemp.put("total_fee", df.format(amount));
        
//		sParaTemp.put("body", body);
//		sParaTemp.put("show_url", show_url);
//		sParaTemp.put("anti_phishing_key", anti_phishing_key);
//		sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
		
		return buildRequestPara(sParaTemp);
	}
	
	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * 
	 * @param notify_id
	 *            通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private String verifyResponse(String notify_id) {
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

		String partner = getAttribute("partner");
		String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner
				+ "&notify_id=" + notify_id;

		return checkUrl(veryfy_url);
	}

	/**
	 * 获取远程服务器ATN结果
	 * 
	 * @param urlvalue
	 *            指定URL路径地址
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String checkUrl(String urlvalue) {
		String inputLine = "";

		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			inputLine = in.readLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
			inputLine = "";
		}

		return inputLine;
	}

	public boolean verify(String sn, HttpServletRequest request) {
		//获取支付宝GET过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		//判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
    	String responseTxt = "false";
    	if(params.get("notify_id") != null) {
			String notifyId = params.get("notify_id");
			responseTxt = verifyResponse(notifyId);
		}
    	
//    	String sign = "";
//	    if(params.get("sign") != null) {
//	    	sign = params.get("sign");
//	    }
	    
//	    boolean isSign = getSignVeryfy(params, sign);
    	
    	if ("true".equals(responseTxt)) {
            return true;
        } else {
            return false;
        }
	}
	
	/**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
	private boolean getSignVeryfy(Map<String, String> Params, String sign) {
    	//过滤空值、sign与sign_type参数
    	Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = MD5.verify(preSignStr, sign, getAttribute("key"), "utf-8");
        
        return isSign;
    }

	public BigDecimal getAmount(String sn, HttpServletRequest request) {
		return new BigDecimal(request.getParameter("total_fee"));
	}

	public String getNotifyContext(String sn, HttpServletRequest request) {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		
		//判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
    	String responseTxt = "false";
		if(params.get("notify_id") != null) {
			String notify_id = params.get("notify_id");
			responseTxt = verifyResponse(notify_id);
		}
		
		if ("true".equals(responseTxt)) {
			return "success";
        } else {
        	return "error";
        }
	}
}