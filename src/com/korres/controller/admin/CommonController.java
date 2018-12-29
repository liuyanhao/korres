package com.korres.controller.admin;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.korres.entity.Area;
import com.korres.service.AreaService;
import com.korres.service.CaptchaService;
import com.korres.service.MemberService;
import com.korres.service.MessageService;
import com.korres.service.OrderService;
import com.korres.service.ProductService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

@Controller("adminCommonController")
@RequestMapping({ "/admin/common" })
public class CommonController implements ServletContextAware {

	@Value("${system.name}")
	private String systemName;

	@Value("${system.version}")
	private String systemVersion;

	@Value("${system.description}")
	private String systemDescription;

	@Value("${system.show_powered}")
	private Boolean systemShowPowered;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "messageServiceImpl")
	private MessageService messageService;

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = { "/main" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String main() {
		return "/admin/common/main";
	}

	@RequestMapping(value = { "/index" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(ModelMap model) {
		model.addAttribute("systemName", this.systemName);
		model.addAttribute("systemVersion", this.systemVersion);
		model.addAttribute("systemDescription", this.systemDescription);
		model.addAttribute("systemShowPowered", this.systemShowPowered);
		model.addAttribute("javaVersion", System.getProperty("java.version"));
		model.addAttribute("javaHome", System.getProperty("java.home"));
		model.addAttribute("osName", System.getProperty("os.name"));
		model.addAttribute("osArch", System.getProperty("os.arch"));
		model.addAttribute("serverInfo", this.servletContext.getServerInfo());
		model.addAttribute("servletVersion",
				this.servletContext.getMajorVersion() + "."
						+ this.servletContext.getMinorVersion());
		model.addAttribute("waitingPaymentOrderCount",
				this.orderService.waitingPaymentCount(null));
		model.addAttribute("waitingShippingOrderCount",
				this.orderService.waitingShippingCount(null));
		model.addAttribute("marketableProductCount", this.productService.count(
				null, Boolean.valueOf(true), null, null,
				Boolean.valueOf(false), null, null));
		model.addAttribute("notMarketableProductCount", this.productService
				.count(null, Boolean.valueOf(false), null, null,
						Boolean.valueOf(false), null, null));
		model.addAttribute(
				"stockAlertProductCount",
				this.productService.count(null, null, null, null,
						Boolean.valueOf(false), null, Boolean.valueOf(true)));
		model.addAttribute(
				"outOfStockProductCount",
				this.productService.count(null, null, null, null,
						Boolean.valueOf(false), Boolean.valueOf(true), null));
		model.addAttribute("memberCount",
				Long.valueOf(this.memberService.count()));
		model.addAttribute("unreadMessageCount",
				this.messageService.count(null, Boolean.valueOf(false)));

		return "/admin/common/index";
	}

	@RequestMapping(value = { "/area" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<Long, String> area(Long parentId) {
		List<Area> listArea = new ArrayList<Area>();
		Area area = this.areaService.find(parentId);
		if (area != null) {
			listArea = new ArrayList<Area>(area.getChildren());
		} else {
			listArea = this.areaService.findRoots();
		}

		HashMap<Long, String> map = new HashMap<Long, String>();
		Iterator<Area> iterator = listArea.iterator();
		while (iterator.hasNext()) {
			Area ar = iterator.next();
			map.put(ar.getId(), ar.getName());
		}

		return map;
	}

	@RequestMapping(value = { "/captcha" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void image(String captchaId, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isEmpty(captchaId)) {
			captchaId = request.getSession().getId();
		}

		String str1 = new StringBuffer().append("yB").append("-").append("der")
				.append("ewoP").reverse().toString();
		String str2 = new StringBuffer().append("ten").append(".")
				.append("xxp").append("ohs").reverse().toString();
		response.addHeader(str1, str2);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0L);
		response.setContentType("image/jpeg");
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			BufferedImage bufferedImage = this.captchaService
					.buildImage(captchaId);
			ImageIO.write(bufferedImage, "jpg", out);
			out.flush();
		} catch (Exception localException1) {
			localException1.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}