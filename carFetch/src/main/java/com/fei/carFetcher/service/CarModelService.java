package com.fei.carFetcher.service;

import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

import com.fei.carFetcher.pojo.CarModel;

/**
 * @author fei
 * @Time：2017年4月6日 下午3:53:28
 * @version 1.0
 */
public interface CarModelService {
	
	CarModel getCarModelByPidAndName(String pid,String name);
	
	@Transactional
	void updateCarModel(CarModel carModel);
	
	@Transactional
	void insertCarModel(CarModel carModel);
	
	void exportCarModel() throws IOException;
	
	void otherOptionsDelete();
	
	void test();
}
