package com.fei.carFetcher.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@RequestMapping("search/{begin}/{end}")
	public void getDate(@PathVariable Character begin,@PathVariable Character end){
		fetcherAutohomeData.get(begin,end);
	}
	
	@RequestMapping("export")
	public void exportCarModel(){
		try {
			carModelService.exportCarModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
