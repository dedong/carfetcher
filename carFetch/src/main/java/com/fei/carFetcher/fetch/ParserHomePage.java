package com.fei.carFetcher.fetch;

import static com.fei.carFetcher.common.Commons.*;

import com.fei.carFetcher.pojo.CarModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

	public static final Pattern pattern = Pattern.compile("(\\d+)");
	public static final Pattern patternContent = Pattern.compile("\\((.*)\\)");

	public static final Logger logger = LoggerFactory.getLogger(ParserHomePage.class);

	/**
	 * 用来解析首页 获取车系首页的数据 http://www.autohome.com.cn/2097/
	 * 
	 * @param url
	 * @throws IOException
	 */
	public static Optional<Map<String, String>> parseHomePage(String url, String path) throws IOException {
		Document document = getDocument(url);
		Map<String, String> homeData = Maps.newLinkedHashMap();
		List<String> oldList = getJsonp(url);
		if (oldList == null)
			return null;
		String carName = homePageData(document);
		Elements liElems = document.select(".nav-item");
		if (liElems.isEmpty())
			return null;
		Elements aElem = liElems.get(1).select("a");
		if (!aElem.isEmpty()) {
			String href = aElem.attr("href");
			homeData.put(href, carName);
			if (logger.isInfoEnabled()) {
				logger.info("href is:{}" + href);
			}
		}
		Optional<Map<String, String>> optHomeData = Optional.of(homeData);
		return optHomeData;
	}

	public static List<List<CarModel>> parseGrayPage(String url) throws IOException {
		List<String> list = ParserStopSalePage.parseStopSaleData(url);
		if (list == null || list.size() == 0) {
			return null;
		}
		List<List<CarModel>> carModels = Lists.newArrayList();
		for (String configLink : list) {
			List<CarModel> parseSpecificPage = null;
			parseSpecificPage = ParserSpecificPage.parseSpecificPage(configLink, "");
			if (parseSpecificPage != null)
				carModels.add(parseSpecificPage);
		}
		return carModels;
	}

	/**
	 * 获取车辆名称
	 * @return
	 */
	private static String homePageData(Document document) {
		String carName = document.select(".subnav-title-name > a").get(0).text();
		return carName;
	}


	private static List<String> getJsonp(String url) throws IOException {
		Matcher matcher = pattern.matcher(url);
		String sid = null;
		if (matcher.find()) {
			sid = matcher.group(1);
		}
		String jsonp = "http://api.che168.com/auto/ForAutoCarPCInterval.ashx?callback=che168CallBack&_appid=cms&sid="
				+ sid + "&yid=0&pid=110000";
		Document document = null;
		try {
			document = getDocument(jsonp);
		} catch (Exception e) {
			if (e instanceof IllegalArgumentException) {
				writeStringtoFile("error_url.txt", url + "\n", true);
			} else {
				throw e;
			}
		}
		if (document == null)
			return null;
		return parseJsonp(document);
	}

	/**
	 * 处理jsonp
	 * {"returncode":0,"message":"","result":{"SId":3343,"YId":0,"minPrice":"4.90","maxPrice":"7.80","url":"http://www.che168.com/china/baojun/baojun610/4_8/?pvareaid=100383","count":8}}
	 *
	 * @param document
	 * @return
	 */
	private static List<String> parseJsonp(Document document) {
		List<String> list = Lists.newArrayListWithCapacity(4);
		String content = document.html();
		Matcher matcher = patternContent.matcher(content);
		if (matcher.find()) {
			content = matcher.group(1);
		}
		content = filter(content);
		if (content.contains("不存在车源信息")) {
			list.add("暂无");
			list.add("暂无");
			list.add("暂无");
			list.add("暂无");
			return list;
		}
		String SId = content.substring(content.indexOf("SId:") + 4, content.indexOf(",YId"));
		String minPrice = content.substring(content.indexOf("minPrice:") + 9, content.indexOf(",maxPrice"));
		String maxPrice = content.substring(content.indexOf("maxPrice:") + 9, content.indexOf(",url"));
		String count = content.substring(content.indexOf("count:") + 6, content.indexOf("}"));
		list.add(minPrice);
		list.add(maxPrice);
		list.add(count);
		list.add(SId);
		return list;
	}

	private static String filter(String content) {
		String cont = content.replaceAll("&", "").replaceAll("quot;", "").replace("\"", "");
		return cont;
	}

}
