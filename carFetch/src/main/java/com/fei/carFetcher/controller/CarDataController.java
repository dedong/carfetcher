package com.fei.carFetcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fei.carFetcher.fetch.FetcherAutohomeData;
import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.service.CarModelService;

/**
 * @author fei
 * @Time：2017年4月6日 下午3:23:32
 * @version 1.0
 */
@RestController
@RequestMapping("car")
public class CarDataController {
	
	@Autowired
	private CarModelService carModelService;
	
	@Autowired
	private FetcherAutohomeData fetcherAutohomeData;
	
	@RequestMapping("search")
	public void getDate(){
		fetcherAutohomeData.get();
	}
	
}
