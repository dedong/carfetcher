package com.fei.carFetcher.fetch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.pojo.ParamItem;
import com.fei.carFetcher.pojo.Specs;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import oracle.net.aso.l;

import org.apache.commons.lang3.StringUtils;
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

import static com.fei.carFetcher.common.Commons.*;

/**
 * 
 * @author fei
 * @version 1.0 Function: TODO
 */
public class ParserSpecificPage {

	public static final Logger logger = LoggerFactory.getLogger(ParserSpecificPage.class);

	/**
	 * 取出一页详情页的数据
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static List<CarModel> parseSpecificPage(String url, String errorPath) throws IOException {
		// System.out.println("正在解析的url:" + url);
		String content = null;
		try {
			content = getDocument(url).toString();
		} catch (Exception e) {
			parseSpecificPage(url, errorPath);
		}
		if (content != null) {
			if (content.contains("抱歉，暂无相关数据。") || content.contains("您访问的页面出错了")) {
				return null;
			}
		}
		Html html = Html.create(content);
		// 取出id 解析其中的js，非贪婪匹配
		try {
			String idList = html.regex("specIDs =\\[(.*?)\\];", 1).get();
			String keyLink = html.regex("keyLink = (.*?);", 1).get();
			String config = html.regex("var config = (.*?);", 1).get();
			String option = html.regex("var option = (.*)var bag", 1).get().replace(";", "");

			System.out.println(config);
			JSONObject parseObject = JSON.parseObject(config);
			JSONObject object = (JSONObject) parseObject.get("result");
			/*
			 * System.out.println(object.getClass());
			 * System.out.println(object);
			 * System.out.println(object.get("paramtypeitems"));
			 * System.out.println(object.get("specsList"));
			 */
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
								if ("车型名称".equals(name)) {
									carModel.setName(str);
								} else if ("级别".equals(name)) {
									carModel.setGrade(str);
								} else if ("变速箱".equals(name)) {
									carModel.setGearbox(str);
								} else if (name.contains("工信部综合油耗")) {
									try {
										double parseDouble = Double.parseDouble(str);
										carModel.setOilConsumption(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if (name.contains("座位数")) {
									try {
										int parseInt = Integer.parseInt(str);
										carModel.setMannedNum(parseInt);
									} catch (Exception e) {
									}
								} else if (name.contains("整备质量")) {
									try {
										double parseDouble = Double.parseDouble(str);
										carModel.setCurbWeight(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if (name.contains("油箱容积")) {
									try {
										double parseDouble = Double.parseDouble(str);
										carModel.setTankCapacity(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if ("排量(L)".equals(name)) {
									try {
										double parseDouble = Double.parseDouble(str);
										carModel.setDisplacement(BigDecimal.valueOf(parseDouble));
									} catch (Exception e) {
									}
								} else if ("燃料形式".equals(name)) {
									if ("汽油".equals(str)) {
										carModel.setEnergy(5);
									} else if ("柴油".equals(str)) {
										carModel.setEnergy(6);
									} else {
										carModel.setEnergy(4);
									}
								} else if ("环保标准".equals(name)) {
									carModel.setEnvStandard(str);
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
			// writeStringtoFile(errorPath,url + "\n",true);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用来处理没有颜色的数据
	 * 
	 * @param ids
	 * @param configList
	 * @param optionList
	 * @return
	 */
	public static List<List<Object>> parseListNoColor(String[] ids, Table<String, String, String> configList,
			Table<String, String, String> optionList, List<String> nameList) {
		List<List<Object>> contentLists = Lists.newArrayList();
		List<Object> contentList = Lists.newArrayList();
		for (String id : ids) {
			parseSpecList(id, configList, optionList, contentList, nameList);
			contentLists.add(contentList);
			contentList = Lists.newArrayList();
		}
		return contentLists;
	}

	/**
	 * /** [[{车型名称={1002785=宝斯通 2014款 3.0TVIP版NGD3.0-C3HA}}, {车型名称={1001002=宝斯通
	 * 2010款 2.2L高级版HFC4GA2-1B}}, {车型名称={1001001=宝斯通 2010款 3.0T高级版NGD3.0-C3HA}}]
	 * [车型名称, 厂商指导价(元), 厂商, 级别, 发动机, 变速箱, 长*宽*高(mm), 车身结构, .....]
	 * 
	 * @param id
	 * @param
	 * @param contentList
	 * @param nameList
	 *            从list里找到name,如果没有name就赋值为""
	 */
	private static void parseSpecList(String id, Table<String, String, String> configTable,
			Table<String, String, String> optionTable, List<Object> contentList, List<String> nameList) {
		for (String name : nameList) {
			String value = configTable.get(name, id);
			if (value == null) {
				value = optionTable.get(name, id);
			}
			contentList.add(value);

		}
		contentList.add("id:" + id);
		contentList.add("http://www.autohome.com.cn/spec/" + id);
	}

	/**
	 * 用于将来的扩展
	 * 
	 * @param id
	 * @param list
	 * @param contentList
	 */
	private static void parseSpecListForColor(String id, List<Map<String, List<String>>> list,
			List<Object> contentList) {
		for (Map<String, List<String>> map : list) {
			List<String> ss = map.get(id);
			if (ss == null)
				continue;
			contentList.add(ss);
		}
	}

	/**
	 * 批量更新
	 * 
	 * @param links
	 * @return
	 * @throws IOException
	 */
	public static void updateData(List<String> links, String errorPath) throws IOException {
		for (String link : links) {
			parseSpecificPage(link, errorPath);

		}
	}

	public static void main(String[] args) throws IOException {
		parseSpecificPage("http://car.autohome.com.cn/config/series/2097.html", "");
	}

}
