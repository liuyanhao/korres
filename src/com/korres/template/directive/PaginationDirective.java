package com.korres.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.korres.util.FreemarkerUtils;
import org.springframework.stereotype.Component;

@Component("paginationDirective")
public class PaginationDirective extends BaseDirective {
	// private static final String PATTERN = "pattern";
	// private static final String PAGE_NUMBER = "pageNumber";
	// private static final String TOTAL_PAGES = "totalPages";
	// private static final String SEGMENT_COUNT = "segmentCount";
	private static final String PATTERN = "pattern";
	private static final String PAGE_NUMBER = "pageNumber";
	private static final String TOTAL_PAGES = "totalPages";
	private static final String SEGMENT_COUNT = "segmentCount";
	private static final String HASPREVIOUS = "hasPrevious";
	private static final String HASNEXT = "hasNext";
	private static final String ISFIRST = "isFirst";
	private static final String ISLAST = "isLast";
	private static final String PREVIOUS_PAGE_NUMBER = "previousPageNumber";
	private static final String NEXT_PAGE_NUMBER = "nextPageNumber";
	private static final String FIRST_PAGE_NUMBER = "firstPageNumber";
	private static final String LAST_PAGE_NUMBER = "lastPageNumber";
	private static final String SEGMENT = "segment";

	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		String pattern = (String) FreemarkerUtils.getParameter(PATTERN,
				String.class, params);
		Integer pageNumber = (Integer) FreemarkerUtils.getParameter(
				PAGE_NUMBER, Integer.class, params);
		Integer totalPages = (Integer) FreemarkerUtils.getParameter(
				TOTAL_PAGES, Integer.class, params);
		Integer segmentCount = (Integer) FreemarkerUtils.getParameter(
				SEGMENT_COUNT, Integer.class, params);
		if ((pageNumber == null) || (pageNumber.intValue() < 1)) {
			pageNumber = Integer.valueOf(1);
		}

		if ((totalPages == null) || (totalPages.intValue() < 1)) {
			totalPages = Integer.valueOf(1);
		}

		if ((segmentCount == null) || (segmentCount.intValue() < 1)) {
			segmentCount = Integer.valueOf(5);
		}

		boolean hasPrevious = pageNumber.intValue() > 1;
		boolean hasNext = pageNumber.intValue() < totalPages.intValue();
		boolean isFirst = pageNumber.intValue() == 1;
		boolean isLast = pageNumber.equals(totalPages);
		int previousPageNumber = pageNumber.intValue() - 1;
		int nextPageNumber = pageNumber.intValue() + 1;
		int firstPageNumber = 1;
		int lastPageNumber = totalPages.intValue();
		int min = pageNumber.intValue()
				- (int) Math.floor((segmentCount.intValue() - 1) / 2.0D);
		int max = pageNumber.intValue()
				+ (int) Math.ceil((segmentCount.intValue() - 1) / 2.0D);

		if (min < 1) {
			min = 1;
		}

		if (max > totalPages.intValue()) {
			max = totalPages.intValue();
		}

		List<Integer> lisegment = new ArrayList<Integer>();
		for (int i = min; i <= max; i++) {
			lisegment.add(Integer.valueOf(i));
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PATTERN, pattern);
		map.put(PAGE_NUMBER, pageNumber);
		map.put(TOTAL_PAGES, totalPages);
		map.put(SEGMENT_COUNT, segmentCount);
		map.put(HASPREVIOUS, Boolean.valueOf(hasPrevious));
		map.put(HASNEXT, Boolean.valueOf(hasNext));
		map.put(ISFIRST, Boolean.valueOf(isFirst));
		map.put(ISLAST, Boolean.valueOf(isLast));
		map.put(PREVIOUS_PAGE_NUMBER, Integer.valueOf(previousPageNumber));
		map.put(NEXT_PAGE_NUMBER, Integer.valueOf(nextPageNumber));
		map.put(FIRST_PAGE_NUMBER, Integer.valueOf(firstPageNumber));
		map.put(LAST_PAGE_NUMBER, Integer.valueOf(lastPageNumber));
		map.put(SEGMENT, lisegment);

		setVariables(map, env, body);
	}
}