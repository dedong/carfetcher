package com.fei.carFetcher.fetch;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.utils.DbUtil;
import com.fei.carFetcher.utils.HttpClientUtil;
import com.fei.carFetcher.utils.PinyinUtil;
import com.google.common.collect.Maps;

/**
 * @author fei
 * @Time：2017年4月13日 上午11:23:18
 * @version 1.0
 */
public class FetchBegin {
	
	public void fetch(String url){
		ParserHomePage parserHomePage = new ParserHomePage();
		ParserSpecificPage pSpecificPage = new ParserSpecificPage();
		try {
			Map<String, CarModel> mapPair = fetchSinglePageLink(url);
			// 处理带有配置参数的页面
			for (Map.Entry<String, CarModel> entry : mapPair.entrySet()) {
				String homeUrl = entry.getKey();
				CarModel carModel = entry.getValue();
				System.out.println(homeUrl);
				System.out.println(carModel.getName());
				System.out.println("id------>"+carModel.getId());
				System.out.println("pid------->"+carModel.getpId());
				List<List<CarModel>> parseGrayPage = parserHomePage.parseGrayPage(homeUrl);
				if(parseGrayPage!=null){
					for (List<CarModel> list : parseGrayPage) {
						for (CarModel carModel2 : list) {
							try {
								saveCarModel(carModel, carModel2);
							} catch (SQLIntegrityConstraintViolationException e) {
								e.printStackTrace();
							}
						}
					}
				}
				Optional<Map<String, String>> priceMap = parserHomePage.parseHomePage(homeUrl, "");
				if (priceMap != null) {
					if (priceMap.isPresent()) {
						priceMap.get().forEach((configUrl, homeData) -> {
							try {
								List<CarModel> lists = pSpecificPage.parseSpecificPage(configUrl, "d:/error.txt");
								System.out.println(homeData);
								if (lists != null) {
									for (CarModel carModel2 : lists) {
									 try {
										saveCarModel(carModel, carModel2);
									} catch (SQLIntegrityConstraintViolationException e) {
										e.printStackTrace();
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

	private void saveCarModel(CarModel carModel, CarModel carModel2) throws SQLIntegrityConstraintViolationException {
		if(carModel2.getName().equals("-")){
			return;
		}
		carModel2.setpId(carModel.getId());
		CarModel oldModel = DbUtil.getCarModelByPidAndName(carModel2.getpId(), carModel2.getName());
		if(oldModel==null){
			if(carModel2.getName()==null)
				return;
			String reName = carModel2.getName().replaceAll("%", "");
			carModel2.setName(reName);
			DbUtil.insertCarModel(carModel2);
		}/*else{
			carModel2.setId(oldModel.getId());
			carModelService.updateCarModel(carModel2);
		}*/
	}

	/**
	 * 解析http://www.autohome.com.cn/grade/carhtml/A.html这种格式的页面 按照三层目录层级排放
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private  Map<String, CarModel> fetchSinglePageLink(String url) throws IOException {
		Map<String, CarModel> carModelMap = Maps.newConcurrentMap();
		//List<CarModel> carModels = new ArrayList<>();
		Document document = getDocument(url);
		Elements dlElems = document.select("dl");
		for (int i = 0; i < dlElems.size(); i++) {
			String firstBrand = dlElems.get(i).select("dt > div > a").text();
			CarModel pinpai = new CarModel();
			pinpai.setType(1);
			pinpai.setName(firstBrand);
			String shouzimu = PinyinUtil.getFirstStr(pinpai.getName());
			pinpai.setpId("0");
			pinpai.setInitialed(shouzimu);
			CarModel carModel = DbUtil.getCarModelByPidAndName(pinpai.getpId(), pinpai.getName());
			if(carModel==null){
				DbUtil.insertCarModel(pinpai);
			}else{
				pinpai.setId(carModel.getId());
			}
		//	carModels.add(pinpai);
			Elements divElems = dlElems.get(i).select("dd > div.h3-tit");
			for (int j = 0; j < divElems.size(); j++) {
				String secondBrand = divElems.get(j).text();
				CarModel changshang = new CarModel();
				changshang.setType(2);
				changshang.setName(secondBrand);
				System.out.println(secondBrand);
				changshang.setpId(pinpai.getId());
				CarModel carModel2 = DbUtil.getCarModelByPidAndName(changshang.getpId(), changshang.getName());
				if(carModel2==null){
					DbUtil.insertCarModel(changshang);
				}else{
					changshang.setId(carModel2.getId());
				}
				//carModels.add(changshang);
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
					//carModels.add(chexi);
					CarModel carModelchexi = DbUtil.getCarModelByPidAndName(chexi.getpId(), chexi.getName());
					if(carModelchexi==null){
						DbUtil.insertCarModel(chexi);
					}else {
						chexi.setId(carModelchexi.getId());
					}
					carModelMap.put(hrefUrl, chexi);
				}

			}
		}
		return carModelMap;
	}
	
	private  Document getDocument(String url) throws IOException {
		String content = HttpClientUtil.sendHttpsGet(url);
		Document document = Jsoup.parse(content);
		return document;

	}
}
