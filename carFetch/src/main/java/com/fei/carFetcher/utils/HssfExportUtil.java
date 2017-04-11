package com.fei.carFetcher.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.javassist.expr.NewArray;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.fei.carFetcher.pojo.CarModelVo;

/**
 * @author fei
 * @Time：2017年4月10日 下午2:01:21
 * @version 1.0
 */
public class HssfExportUtil {

	/**
	 * 导出车型数据到xls exportCarModel:().
	 * 
	 * @author fei
	 * @Time：2017年4月10日 下午2:06:39
	 * @param carModelVos
	 * @throws IOException
	 */
	public static void exportCarModel(List<CarModelVo> carModelVos) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();

		HSSFSheet sheet = workbook.createSheet("车型数据");

		sheet.setDefaultColumnWidth(13);

		HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
		Font font = workbook.createFont();
		font.setFontName("黑体");
		font.setFontHeightInPoints((short) 21);
		style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
		style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

		HSSFRow row = sheet.createRow(0);// 标题行
		HSSFCell createCell = row.createCell(0);
		createCell.setCellStyle(style);
		createCell.setCellValue("车型数据");

		List<String> titles = new ArrayList<>();
		titles.add("编号");
		titles.add("品牌");
		titles.add("厂商");
		titles.add("车系");
		titles.add("车型名称");
		titles.add("级别");
		titles.add("排气量");
		titles.add("载人数量");
		titles.add("总质量");
		titles.add("百公里油耗");
		titles.add("变速箱");
		titles.add("能源种类");
		titles.add("环保标准");
		titles.add("油箱容积");

		HSSFRow row2 = sheet.createRow(1);
		for (int i = 0; i < titles.size(); i++) {
			HSSFCell hssfCell = row2.createCell(i);
			hssfCell.setCellValue(titles.get(i));
		}

		for (int i = 0; i < carModelVos.size(); i++) {
			HSSFRow row3 = sheet.createRow(i + 2);
			CarModelVo carModelVo = carModelVos.get(i);
			HSSFCell cell0 = row3.createCell(0);
			cell0.setCellValue(i + 1);
			HSSFCell cell1 = row3.createCell(1);
			cell1.setCellValue(carModelVo.getPinpai());
			HSSFCell cell2 = row3.createCell(2);
			cell2.setCellValue(carModelVo.getChanshang());
			HSSFCell cell3 = row3.createCell(3);
			cell3.setCellValue(carModelVo.getChexi());
			HSSFCell cell4 = row3.createCell(4);
			cell4.setCellValue(carModelVo.getName());
			HSSFCell cell5 = row3.createCell(5);
			cell5.setCellValue(carModelVo.getGrade());
			HSSFCell cell6 = row3.createCell(6);
			BigDecimal displacement = carModelVo.getDisplacement();
			if (displacement != null) {
				cell6.setCellValue(displacement.doubleValue());
			} else {
				cell6.setCellValue("-");
			}
			HSSFCell cell7 = row3.createCell(7);
			cell7.setCellValue(carModelVo.getMannedNum());
			HSSFCell cell8 = row3.createCell(8);
			BigDecimal curbWeight = carModelVo.getCurbWeight();
			if (curbWeight != null) {
				cell8.setCellValue(curbWeight.doubleValue());
			} else {
				cell8.setCellValue("-");
			}
			HSSFCell cell9 = row3.createCell(9);
			BigDecimal oilConsumption = carModelVo.getOilConsumption();
			if (oilConsumption != null) {
				cell9.setCellValue(oilConsumption.doubleValue());
			} else {
				cell9.setCellValue("-");
			}
			HSSFCell cell10 = row3.createCell(10);
			cell10.setCellValue(carModelVo.getGearbox());
			HSSFCell cell11 = row3.createCell(11);
			Integer energy = carModelVo.getEnergy();
			String energystr = "汽油";
			if (energy == 5) {
			} else if (energy == 6) {
				energystr = "柴油";
			} else {
				energystr = "其他能源";
			}
			cell11.setCellValue(energystr);
			HSSFCell cell12 = row3.createCell(12);
			cell12.setCellValue(carModelVo.getEnvStandard());
			HSSFCell cell13 = row3.createCell(13);
			BigDecimal tankCapacity = carModelVo.getTankCapacity();
			if (tankCapacity!=null) {
				cell13.setCellValue(tankCapacity.doubleValue());
			}else{
				cell13.setCellValue("-");
			}

			System.out.println("导出第" + i + "条数据！");
		}

		FileOutputStream outputStream = new FileOutputStream(new File("d://车型数据.xls"));

		workbook.write(outputStream);

		outputStream.flush();
		outputStream.close();
	}

}
