package com.fei.carFetcher.fetch;

import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.pojo.StopSale;
import com.fei.carFetcher.service.CarModelService;
import com.fei.carFetcher.utils.HttpClientUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.fei.carFetcher.common.Commons.writeStringtoFile;


@Service
public class FetcherAutohomeData {

	public static final Logger logger = LoggerFactory.getLogger(FetcherAutohomeData.class);

	public static final Pattern pattern = Pattern.compile("(\\d+)");
	
	@Autowired
	private CarModelService carModelService;

	private  Document getDocument(String url) throws IOException {
		String content = HttpClientUtil.sendHttpsGet(url);
		Document document = Jsoup.parse(content);
		return document;

	}

	/**
	 * 得到所有的页面
	 * 
	 * @return
	 */
	private  List<String> pages() {
		List<String> urls = Lists.newArrayList();
		for (char i = 65; i < 91; i++) {
			String url = "http://www.autohome.com.cn/grade/carhtml/" + i + ".html";
			urls.add(url);
		}
		urls.remove("http://www.autohome.com.cn/grade/carhtml/E.html");
		urls.remove("http://www.autohome.com.cn/grade/carhtml/I.html");
		urls.remove("http://www.autohome.com.cn/grade/carhtml/U.html");
		urls.remove("http://www.autohome.com.cn/grade/carhtml/V.html");
		Collections.sort(urls);
		return urls;
	}

	/**
	 * 解析http://www.autohome.com.cn/grade/carhtml/A.html这种格式的页面 按照三层目录层级排放
	 * http://car1.autoimg.cn/logo/brand/50/129472203719848750.jpg,奥迪,奥迪(进口),Crosslane
	 * Coupe,http://www.autohome.com.cn/2908/#levelsource=000000000_0&pvareaid=101594
	 * http://car1.autoimg.cn/logo/brand/50/129472203719848750.jpg,奥迪,奥迪(进口),奥迪TT
	 * offroad,http://www.autohome.com.cn/3479/#levelsource=000000000_0&pvareaid=101594
	 * http://car1.autoimg.cn/logo/brand/50/129472203719848750.jpg,奥迪,奥迪(进口),e-tron
	 * quattro,http://www.autohome.com.cn/3894/#levelsource=000000000_0&pvareaid=101594
	 * http://car1.autoimg.cn/logo/brand/50/129472203719848750.jpg,奥迪,奥迪(进口),Nanuk,http://www.autohome.com.cn/3210/#levelsource=000000000_0&pvareaid=101594
	 * http://car1.autoimg.cn/logo/brand/50/129472203719848750.jpg,奥迪,奥迪(进口),quattro,http://www.autohome.com.cn/2218/#levelsource=000000000_0&pvareaid=101594
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private  Map<String, CarModel> fetchSinglePageLink(String url) throws IOException {
		Map<String, String> singlePageMap = Maps.newLinkedHashMap();
		Map<String, String> grayPageMap = Maps.newLinkedHashMap();
		Map<String, CarModel> carModelMap = Maps.newConcurrentMap();
		List<CarModel> carModels = new ArrayList<>();
		List<Map<String, String>> lists = Lists.newArrayListWithCapacity(2);
		Document document = getDocument(url);
		Elements dlElems = document.select("dl");
		for (int i = 0; i < dlElems.size(); i++) {
			String img = dlElems.get(i).select("dt > a > img").attr("src");
			String firstBrand = dlElems.get(i).select("dt > div > a").text();
			CarModel pinpai = new CarModel();
			pinpai.setType(1);
			pinpai.setName(firstBrand);
			pinpai.setpId("0");
			CarModel carModel = carModelService.getCarModelByPidAndName(pinpai.getpId(), pinpai.getName());
			if(carModel==null){
				carModelService.insertCarModel(pinpai);
			}else{
				pinpai.setId(carModel.getId());
			}
			carModels.add(pinpai);
			String href = dlElems.get(i).select("dt > div > a").attr("href");
			String firstBrandId = null;
			Matcher matcher = pattern.matcher(href);
			if (matcher.find()) {
				firstBrandId = matcher.group(1);
			}
			Elements divElems = dlElems.get(i).select("dd > div.h3-tit");
			for (int j = 0; j < divElems.size(); j++) {
				String secondBrand = divElems.get(j).text();
				CarModel changshang = new CarModel();
				changshang.setType(2);
				changshang.setName(secondBrand);
				System.out.println(secondBrand);
				changshang.setpId(pinpai.getId());
				CarModel carModel2 = carModelService.getCarModelByPidAndName(changshang.getpId(), changshang.getName());
				if(carModel2==null){
					carModelService.insertCarModel(changshang);
				}else{
					changshang.setId(carModel2.getId());
				}
				carModels.add(changshang);
				Element ulElem = dlElems.get(i).select("dd > ul").get(j);
				Elements liElems = ulElem.select("li");
				for (Element liElem : liElems) {
					Elements aElems = liElem.select("h4 > a");
					if (aElems.text().isEmpty()) {
						continue;
					}
					String thirdBrand = "";
					if (aElems.hasClass("greylink")) {
						thirdBrand = aElems.text();

						grayPageMap.put(aElems.attr("href").replace("#levelsource=000000000_0&pvareaid=101594", ""),
								"\"" + firstBrand + "\"" + "," + "\"" + firstBrandId + "\"" + "," + "\"" + img + "\""
										+ "," + "\"" + secondBrand + "\"" + "," + "\"" + thirdBrand + "\"");
					} else {
						thirdBrand = aElems.text();
						singlePageMap.put(aElems.attr("href").replace("#levelsource=000000000_0&pvareaid=101594", ""),
								"\"" + firstBrand + "\"" + "," + "\"" + firstBrandId + "\"" + "," + "\"" + img + "\""
										+ "," + "\"" + secondBrand + "\"" + "," + "\"" + thirdBrand + "\"");
					}
					String hrefUrl = aElems.attr("href").replace("#levelsource=000000000_0&pvareaid=101594", "");
					CarModel chexi = new CarModel();
					chexi.setType(3);
					chexi.setName(thirdBrand);
					chexi.setpId(changshang.getId());
					carModels.add(chexi);
					CarModel carModelchexi = carModelService.getCarModelByPidAndName(chexi.getpId(), chexi.getName());
					if(carModelchexi==null){
						carModelService.insertCarModel(chexi);
					}else {
						chexi.setId(carModelchexi.getId());
					}
					carModelMap.put(hrefUrl, chexi);
				}

			}
		}

		lists.add(singlePageMap);
		lists.add(grayPageMap);
		return carModelMap;
	}

	/**
	 * 解析程序的入口 只要调用这个函数就可以获取全部数据
	 * 
	 * @param salePath
	 *            写入在售车型数据
	 * @param stopSalePath
	 *            写入停售车型数据
	 * @return
	 * @throws IOException
	 */
	public void get() {
		List<String> pages = pages();
		// pages.parallelStream().parallel().forEach(pageUrl -> {
		for (String pageUrl : pages) {
			try {
				Map<String, CarModel> mapPair = fetchSinglePageLink(pageUrl);
				/*
				 * Map<String, String> singleMap = mapPair.get(0); Map<String,
				 * String> grayMap = mapPair.get(1);
				 */
				// 处理带有配置参数的页面
				for (Map.Entry<String, CarModel> entry : mapPair.entrySet()) {
					String homeUrl = entry.getKey();
					CarModel carModel = entry.getValue();
					System.out.println(homeUrl);
					System.out.println(carModel.getName());
					System.out.println("id------>"+carModel.getId());
					System.out.println("pid------->"+carModel.getpId());
					Map<StopSale, List<CarModel>> stopSaleListMap = ParserHomePage.parseGrayPage(homeUrl);
					if(stopSaleListMap!=null){
					for (Map.Entry<StopSale, List<CarModel>> entry2 : stopSaleListMap.entrySet()) {
						System.out.println(entry2.getKey());
						for (CarModel carMode2 : entry2.getValue()) {
							carMode2.setpId(carModel.getId());
							CarModel oldModel = carModelService.getCarModelByPidAndName(carMode2.getpId(), carMode2.getName());
							if(oldModel==null){
								carModelService.insertCarModel(carMode2);
							}else{
								carMode2.setId(oldModel.getId());
								carModelService.updateCarModel(carMode2);
							}
						}
					}
					}
					// parseMap(stopSaleListMap, data, stopSalePath);
					Optional<Map<String, String>> priceMap = ParserHomePage.parseHomePage(homeUrl, "");
					if (priceMap != null) {
						if (priceMap.isPresent()) {
							priceMap.get().forEach((configUrl, homeData) -> {
								try {
									List<CarModel> lists = ParserSpecificPage.parseSpecificPage(configUrl, "d:/error.txt");
									System.out.println(homeData);
									if (lists != null) {
										for (CarModel carModel2 : lists) {
											carModel2.setpId(carModel.getId());
											CarModel oldModel = carModelService.getCarModelByPidAndName(carModel2.getpId(), carModel2.getName());
											if(oldModel==null){
												carModelService.insertCarModel(carModel2);
											}else{
												carModel2.setId(oldModel.getId());
												carModelService.updateCarModel(carModel2);
											}
										}
									}
								} catch (IOException e) {
								}
							});
						}
					}
				}
			} catch (IOException e) {
			}
		}
	}

	private static void parseMap(Map<StopSale, List<List<Object>>> map, String data, String stopSalePath)
			throws IOException {
		if (map == null) {
			String s = "finally value:(only data)" + data;
			System.out.println(s);
			String write = data + "\n";
			writeStringtoFile(stopSalePath, write, true);
		} else {
			map.forEach((stopSale, lists) -> {
				if (lists == null) {
					String s = "finally value:(data and stopSale)" + data + "," + stopSale;
					System.out.println(s);
					String write = data + "," + stopSale + "\n";
					try {
						writeStringtoFile(stopSalePath, write, true);
					} catch (IOException e) {
					}

				} else {
					lists.forEach(list -> {
						list = list.stream().map(obj -> "\"" + obj + "\"" + ",").collect(Collectors.toList());
						String lst = "";
						for (Object o : list) {
							lst += o;
						}
						String s = "finally value:(all)" + data + "," + stopSale + "," + lst;
						System.out.println(s);
						String write = data + "," + stopSale + "," + lst + "\n";
						try {
							writeStringtoFile(stopSalePath, write, true);
						} catch (IOException e) {
						}
					});
				}
			});
		}
	}

	public static void main(String[] args) throws IOException {
		/*
		 * Scanner scanner = new Scanner(System.in);
		 * System.out.println("在售数据的存储路径:"); String salePath = scanner.next();
		 * System.out.println("停售数据的存储路径:"); String stopSalePath =
		 * scanner.next(); System.out.println("错误的URL存储路径"); String errorPath =
		 * scanner.next();
		 */
		//FetcherAutohomeData.get("d:/tmp/nosalecar.txt", "d:/tmp/salecar.txt", "d:/tmp/errorcar.txt");
	}
}
