package com.korres;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/*
 * 类名：ExcelView.java
 * 功能说明：excel公共类
 * 创建日期：2013-8-9 下午01:57:25
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class ExcelView extends AbstractExcelView {
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private String filename;
	private String sheetName;
	private String[] properties;
	private String[] titles;
	private Integer[] widths;
	private Converter[] converters;
	private Collection<?> data;
	private String[] contents;

	static {
		DateConverter dateConverter = new DateConverter();
		dateConverter.setPattern(DATE_PATTERN);
		ConvertUtils.register(dateConverter, Date.class);
	}

	public ExcelView(String filename, String sheetName, String[] properties,
			String[] titles, Integer[] widths, Converter[] converters,
			Collection<?> data, String[] contents) {
		this.filename = filename;
		this.sheetName = sheetName;
		this.properties = properties;
		this.titles = titles;
		this.widths = widths;
		this.converters = converters;
		this.data = data;
		this.contents = contents;
	}

	public ExcelView(String[] properties, String[] titles, Collection<?> data,
			String[] contents) {
		this.properties = properties;
		this.titles = titles;
		this.data = data;
		this.contents = contents;
	}

	public ExcelView(String[] properties, String[] titles, Collection<?> data) {
		this.properties = properties;
		this.titles = titles;
		this.data = data;
	}

	public ExcelView(String[] properties, Collection<?> data) {
		this.properties = properties;
		this.data = data;
	}

	public void buildExcelDocument(Map<String, Object> model,
			HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		 Assert.notEmpty(this.properties);
		 HSSFSheet sheet;
		 if (StringUtils.isNotEmpty(this.sheetName)){
			 sheet = workbook.createSheet(this.sheetName);
		 }
		 else{
			 sheet = workbook.createSheet();
		 }
		 
		 int i = 0;
//		 Object localObject1;
//		 Object localObject2;
//		 Object localObject3;
//		 Object localObject4;
//		 Object localObject5;
		 if ((this.titles != null) && (this.titles.length > 0)) {
			 HSSFRow row = sheet.createRow(i);
			 row.setHeight((short)400);
			 for (int j = 0; j < this.properties.length; j++) {
				 HSSFCell  cell = row.createCell(j);
				 HSSFCellStyle style = workbook.createCellStyle();
				 style.setFillForegroundColor((short)31);
				 style.setFillPattern((short)1);
				 style.setAlignment((short)2);
				 style.setVerticalAlignment((short)1);
				 HSSFFont font = workbook.createFont();
				 font.setFontHeightInPoints((short)11);
				 font.setBoldweight((short)700);
				 style.setFont(font);
				 cell.setCellStyle(style);
				 if (j == 0) {
					 HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
					 HSSFComment comment = patriarch.createComment(new
					 HSSFClientAnchor(0, 0, 0, 0, (short)1, 1, (short)4, 4));
					 comment.setString(new
				     HSSFRichTextString("Powered By KORRES"));
					 cell.setCellComment(comment);
				 }
				 if ((this.titles.length > j) && (this.titles[j] != null)){
					 cell.setCellValue(this.titles[j]);
				 }
				 else{
					 cell.setCellValue(this.properties[j]);
				 }
				 if ((this.widths != null) && (this.widths.length > j) &&
				 (this.widths[j] != null)){
					 sheet.setColumnWidth(j, this.widths[j].intValue());
				 }
				 else{
					 sheet.autoSizeColumn(j);
				 }
			 }
			 
			 i++;
		 }
		 
		 if (this.data != null) {
			 Iterator iterator = this.data.iterator();
			 while (iterator.hasNext()) {
				 Object obj = iterator.next();
				 HSSFRow row = sheet.createRow(i);
				 try {
					for (int n = 0; n < this.properties.length; n++) {
						 HSSFCell cell = row.createCell(n);
						 if ((this.converters != null) && (this.converters.length > n) &&
						 (this.converters[n] != null)) {
							 Class clazz = PropertyUtils.getPropertyType(obj, this.properties[n]);
							 ConvertUtils.register(this.converters[n], clazz);
							 cell.setCellValue(BeanUtils.getProperty(obj, this.properties[n]));
							 ConvertUtils.deregister(clazz);
							 if (clazz.equals(Date.class)) {
								 DateConverter dateConverter = new DateConverter();
								 dateConverter.setPattern("yyyy-MM-dd HH:mm:ss");
								 ConvertUtils.register(dateConverter, Date.class);
							 }
						 }
						 else {
							 cell.setCellValue(BeanUtils.getProperty(obj, this.properties[n]));
						 }
						 if ((i == 0) || (i == 1))
						 if ((this.widths != null) && (this.widths.length > n) &&
						 (this.widths[n] != null)){
							 sheet.setColumnWidth(n, this.widths[n].intValue());
						 }
						 else{
							 sheet.autoSizeColumn(n);
						 }
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			 
		 		i++;
			 }
		 }
		 
		 if ((this.contents != null) && (this.contents.length > 0)) {
			 i++;
			 for (String str : this.contents) {
				 HSSFRow row = sheet.createRow(i);
				 HSSFCell cell = row.createCell(0);
				 HSSFCellStyle style = workbook.createCellStyle();
				 HSSFFont font = workbook.createFont();
				 font.setColor((short)23);
				 style.setFont(font);
				 cell.setCellStyle(style);
				 cell.setCellValue(str);
				 i++;
			 }
		 }
		 
		 response.setContentType("application/force-download");
		 if (StringUtils.isNotEmpty(this.filename)){
			 response.setHeader("Content-disposition", "attachment; filename=" +
			 URLEncoder.encode(this.filename, "UTF-8"));
		 }
		 else{
			 response.setHeader("Content-disposition", "attachment");
		 }
	}

	public String getFileName() {
		return this.filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}

	public String getSheetName() {
		return this.sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String[] getProperties() {
		return this.properties;
	}

	public void setProperties(String[] properties) {
		this.properties = properties;
	}

	public String[] getTitles() {
		return this.titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public Integer[] getWidths() {
		return this.widths;
	}

	public void setWidths(Integer[] widths) {
		this.widths = widths;
	}

	public Converter[] getConverters() {
		return this.converters;
	}

	public void setConverters(Converter[] converters) {
		this.converters = converters;
	}

	public Collection<?> getData() {
		return this.data;
	}

	public void setData(Collection<?> data) {
		this.data = data;
	}

	public String[] getContents() {
		return this.contents;
	}

	public void setContents(String[] contents) {
		this.contents = contents;
	}
}