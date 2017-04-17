package com.fei.carFetcher.fetch;


import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.utils.HttpClientUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHomePage {

	private ParserStopSalePage pStopSalePage = new ParserStopSalePage();
	private ParserSpecificPage parserSpecific = new ParserSpecificPage();

	/**
	 * 用来解析首页 获取车系首页的数据 http://www.autohome.com.cn/2097/
	 * 
	 * @param url
	 * @throws IOException
	 */
	public  Optional<Map<String, String>> parseHomePage(String url, String path) throws IOException {
		Document document;
		try {
			document = getDocument(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Map<String, String> homeData = Maps.newLinkedHashMap();
		String carName = homePageData(document);
		Elements liElems = document.select(".nav-item");
		if (liElems.isEmpty())
			return null;
		Elements aElem = liElems.get(1).select("a");
		if (!aElem.isEmpty()) {
			String href = aElem.attr("href");
			homeData.put(href, carName);
		}
		Optional<Map<String, String>> optHomeData = Optional.of(homeData);
		return optHomeData;
	}

	public  List<List<CarModel>> parseGrayPage(String url) throws IOException {
		List<List<CarModel>> carModels = Lists.newArrayList();
		
		String link = stopSaleLink(url, "");
		
		System.out.println("");
		List<String> list = pStopSalePage.parseStopSaleData(url);
		if (StringUtils.isNotBlank(link)) {
			List<String> saleList = pStopSalePage.parseStopSaleData(link);
			list.addAll(saleList);
		}
		if (list.size() == 0) {
			return null;
		}
		for (String configLink : list) {
			List<CarModel> parseSpecificPage = null;
			parseSpecificPage = parserSpecific.parseSpecificPage(configLink, "");
			if (parseSpecificPage != null)
				carModels.add(parseSpecificPage);
		}
		return carModels;
	}
	
	
	/**
	 * 解析车系页面 获取停售页面链接
	 * 
	 * @param url
	 *            车系页面链接
	 * @param path
	 *            保存的文件路径
	 * @throws IOException
	 */
	private String stopSaleLink(String url, String path) throws IOException {
		try {

			Document document = getDocument(url);
			String href = document.select(".other-car").select(".link-sale").attr("href");
			if (href.isEmpty())
				return null;
			String link = "http://www.autohome.com.cn" + href;
			return link;
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 获取车辆名称
	 * @return
	 */
	private String homePageData(Document document) {
		String carName = document.select(".subnav-title-name > a").get(0).text();
		return carName;
	}

	private Document getDocument(String url) throws IOException {
	        String content = HttpClientUtil.sendHttpsGet(url);
	        Document document = Jsoup.parse(content);
	        return document;

	    }
	
}
