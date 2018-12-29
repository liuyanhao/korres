package com.korres.controller.admin;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.annotation.Resource;

import com.korres.service.OrderService;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminSalesController")
@RequestMapping( { "/admin/sales" })
public class SalesController extends BaseController {
	private static final int IIIlllIl = 12;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(SalesControllerType type, Date beginDate, Date endDate,
			Model model) {
		if (type == null) {
			type = SalesControllerType.month;
		}

		if (beginDate == null) {
			beginDate = DateUtils.addMonths(new Date(), -11);
		}

		if (endDate == null) {
			endDate = new Date();
		}

		LinkedHashMap<Date, Object> salesAmountMap = new LinkedHashMap<Date, Object>();
		LinkedHashMap<Date, Object> salesVolumeMap = new LinkedHashMap<Date, Object>();
		Calendar c1 = DateUtils.toCalendar(beginDate);
		Calendar c2 = DateUtils.toCalendar(endDate);
		int i = c1.get(1);
		int j = c2.get(1);
		int k = c1.get(2);
		int m = c2.get(2);
		for (int n = i; n <= j; n++) {
			if (salesAmountMap.size() >= 12) {
				break;
			}

			Calendar c3 = Calendar.getInstance();
			c3.set(1, n);
			if (type == SalesControllerType.year) {
				c3.set(2, c3.getActualMinimum(2));
				c3.set(5, c3.getActualMinimum(5));
				c3.set(11, c3.getActualMinimum(11));
				c3.set(12, c3.getActualMinimum(12));
				c3.set(13, c3.getActualMinimum(13));
				Date d1 = c3.getTime();
				c3.set(2, c3.getActualMaximum(2));
				c3.set(5, c3.getActualMaximum(5));
				c3.set(11, c3.getActualMaximum(11));
				c3.set(12, c3.getActualMaximum(12));
				c3.set(13, c3.getActualMaximum(13));
				Date d2 = c3.getTime();
				BigDecimal salesAmount = this.orderService.getSalesAmount(d1,
						d2);
				Integer salesVolume = this.orderService.getSalesVolume(d1, d2);
				salesAmountMap.put(d1, salesAmount != null ? salesAmount
						: BigDecimal.ZERO);
				salesVolumeMap.put(d1, Integer
						.valueOf(salesVolume != null ? salesVolume.intValue()
								: 0));
			} else {
				for (int i1 = n == i ? k : c3.getActualMinimum(2); i1 <= (n == j ? m
						: c3.getActualMaximum(2)); i1++) {
					if (salesAmountMap.size() >= 12) {
						break;
					}

					c3.set(2, i1);
					c3.set(5, c3.getActualMinimum(5));
					c3.set(11, c3.getActualMinimum(11));
					c3.set(12, c3.getActualMinimum(12));
					c3.set(13, c3.getActualMinimum(13));
					Date d1 = c3.getTime();
					c3.set(5, c3.getActualMaximum(5));
					c3.set(11, c3.getActualMaximum(11));
					c3.set(12, c3.getActualMaximum(12));
					c3.set(13, c3.getActualMaximum(13));
					Date d2 = c3.getTime();
					BigDecimal salesAmount = this.orderService.getSalesAmount(
							d1, d2);
					Integer salesVolume = this.orderService.getSalesVolume(d1,
							d2);
					salesAmountMap.put(d1, salesAmount != null ? salesAmount
							: BigDecimal.ZERO);
					salesVolumeMap.put(d1, Integer
							.valueOf(salesVolume != null ? salesVolume
									.intValue() : 0));
				}
			}
		}

		model.addAttribute("types", SalesControllerType.values());
		model.addAttribute("type", type);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("salesAmountMap", salesAmountMap);
		model.addAttribute("salesVolumeMap", salesVolumeMap);

		return "/admin/sales/view";
	}

	public enum SalesControllerType {
		year, month;
	}
}