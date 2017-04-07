package com.fei.carFetcher.fetch;

import com.fei.carFetcher.pojo.CarModel;
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
	 * @param end 
	 * @param begin 
	 * 
	 * @return
	 */
	private  List<String> pages(Character begin, Character end) {
		List<String> urls = Lists.newArrayList();
		for (char i = begin; i <= end; i++) {
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
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private  Map<String, CarModel> fetchSinglePageLink(String url) throws IOException {
		Map<String, CarModel> carModelMap = Maps.newConcurrentMap();
		List<CarModel> carModels = new ArrayList<>();
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
					thirdBrand = aElems.text();
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
		return carModelMap;
	}

	/**
	 * 解析程序的入口 只要调用这个函数就可以获取全部数据
	 * @return
	 * @throws IOException
	 */
	public void get(Character begin,Character end) {
		List<String> pages = pages(begin,end);
		for (String pageUrl : pages) {
			try {
				Map<String, CarModel> mapPair = fetchSinglePageLink(pageUrl);
				// 处理带有配置参数的页面
				for (Map.Entry<String, CarModel> entry : mapPair.entrySet()) {
					String homeUrl = entry.getKey();
					CarModel carModel = entry.getValue();
					System.out.println(homeUrl);
					System.out.println(carModel.getName());
					System.out.println("id------>"+carModel.getId());
					System.out.println("pid------->"+carModel.getpId());
					List<List<CarModel>> parseGrayPage = ParserHomePage.parseGrayPage(homeUrl);
					if(parseGrayPage!=null){
						for (List<CarModel> list : parseGrayPage) {
							for (CarModel carModel2 : list) {
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
					}
					/*Optional<Map<String, String>> priceMap = ParserHomePage.parseHomePage(homeUrl, "");
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
					}*/
				}
			} catch (IOException e) {
			}
		}
	}


	public static void main(String[] args) throws IOException {
		new FetcherAutohomeData().get('A','Z');
	}
}
