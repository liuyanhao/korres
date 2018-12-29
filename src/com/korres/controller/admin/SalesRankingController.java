package com.korres.controller.admin;

import java.util.Date;
import javax.annotation.Resource;
import com.korres.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;

@Controller("adminSalesRankingController")
@RequestMapping( { "/admin/sales_ranking" })
public class SalesRankingController extends BaseController {

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Date beginDate, Date endDate, Pageable pageable,
			Model model) {
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("page", this.productService.findSalesPage(beginDate,
				endDate, pageable));

		return "/admin/sales_ranking/list";
	}
}