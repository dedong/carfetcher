package com.fei.carFetcher.fetch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.pojo.ParamItem;
import com.fei.carFetcher.pojo.Specs;
import com.fei.carFetcher.utils.HttpClientUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import oracle.net.aso.l;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author fei
 * @version 1.0 Function: TODO
 */
public class ParserSpecificPage {
	
	//名称
	private final static String NAME_PREX = "<span class='hs_kw0_configpl'></span><span class='hs_kw1_configpl'></span>";
	
	//燃油形式
	private final static String ENERGY_NAME = "燃料形式";
	/**
	 * 取出一页详情页的数据
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public  List<CarModel> parseSpecificPage(String url, String errorPath) throws IOException {
		// System.out.println("正在解析的url:" + url);
		String content = null;
		try {
			content = getDocument(url).toString();
		} catch (Exception e) {
			//parseSpecificPage(url, errorPath);
		}
		if (content != null) {
			if (content.contains("抱歉，暂无相关数据。") || content.contains("您访问的页面出错了")) {
				return null;
			}
		}
		// 取出id 解析其中的js
		try {
			Html html = Html.create(content);
			String idList = html.regex("specIDs =\\[(.*?)\\];", 1).get();
			String keyLink = html.regex("keyLink = (.*?);", 1).get();
			String config = html.regex("var config = (.*?);", 1).get();
			String option = html.regex("var option = (.*)var bag", 1).get().replace(";", "");

			System.out.println(config);
			JSONObject parseObject = JSON.parseObject(config);
			JSONObject object = (JSONObject) parseObject.get("result");
			/*
			 System.out.println(object.getClass());
			 System.out.println(object);
			 System.out.println(object.get("paramtypeitems"));
			 System.out.println(object.get("specsList"));*/
			List<Specs> specsList = JSON.parseArray(object.get("specsList").toString(), Specs.class);
			JSONArray parseArray = JSON.parseArray(object.get("paramtypeitems").toString());

			// JSONArray array = JSON.parseArray(p.toString());
			List<CarModel> carModels = new ArrayList<>();
			for (Specs specs : specsList) {
				String specsId = specs.getSpecid();
				CarModel carModel = new CarModel();
				carModel.setType(4);
				for (Object object2 : parseArray) {
					JSONObject obj = (JSONObject) object2;
					String param = obj.get("paramitems").toString();
					// System.out.println(param);
					List<ParamItem> list = JSON.parseArray(param, com.fei.carFetcher.pojo.ParamItem.class);
					for (ParamItem paramItem : list) {
						String name = paramItem.getName();
						// System.out.println(name);
						List<Map<String, String>> valueitems = paramItem.getValueitems();
						for (Map<String, String> map : valueitems) {
							String id = map.get("specid");
							if (specsId.equals(id)) {
								String str = map.get("value");
								if (NAME_PREX.equals(name)) {
									//TODO
									carModel.setName(replaceSpan(str, "%"));
								} else if ("级别".equals(name)) {
									carModel.setGrade(replaceSpan(str, ""));
								} else if ("变速箱".equals(name)) {
									carModel.setGearbox(replaceSpan(str, ""));
								} else if (name.contains("工信部")) {
									try {
										double parseDouble = Double.parseDouble(replaceSpan(str, ""));
										carModel.setOilConsumption(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if (name.contains("座位数")) {
									try {
										int parseInt = Integer.parseInt(replaceSpan(str, ""));
										carModel.setMannedNum(parseInt);
									} catch (Exception e) {
									}
								} else if (name.contains("整备质量")) {
									try {
										double parseDouble = Double.parseDouble(replaceSpan(str, ""));
										carModel.setCurbWeight(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if (name.contains("油箱容积")) {
									try {
										double parseDouble = Double.parseDouble(replaceSpan(str, ""));
										carModel.setTankCapacity(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if (name.contains("(L)")) {
									try {
										double parseDouble = Double.parseDouble(replaceSpan(str, ""));
										carModel.setDisplacement(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if (ENERGY_NAME.equals(name)) {
									if ("汽油".equals(replaceSpan(str, ""))) {
										carModel.setEnergy(5);
									} else if ("柴油".equals(replaceSpan(str, ""))) {
										carModel.setEnergy(6);
									} else {
										carModel.setEnergy(5);
									}
								} else if ("环保标准".equals(name)) {
									carModel.setEnvStandard(replaceSpan(str, "国"));
								}else if(replaceSpan(name, "油").equals("油标油")){
									carModel.setOilLv(replaceSpan(str, ""));
								}
							} else {
								continue;
							}
						}
					}
				}
				carModels.add(carModel);
			}

			return carModels;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	  private Document getDocument(String url) throws IOException {
	        String content = HttpClientUtil.sendHttpsGet(url);
	        Document document = Jsoup.parse(content);
	        return document;

	    }
	  
	  private String replaceSpan(String value,String prex){
		  String patternContent = "<span class='(.{0,20})'></span>";
		  Pattern pattern = Pattern.compile(patternContent);
		  Matcher matcher = pattern.matcher(value);
		  String replaceAll = matcher.replaceAll(prex);
		  return replaceAll;
	  }

	  
	public static void main(String[] args) throws IOException {
		//parseSpecificPage("http://car.autohome.com.cn/config/series/2097.html", "");
	}

}
