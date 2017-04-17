package com.fei.carFetcher.utils;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fei.carFetcher.mapper.CarModelMapper;
import com.fei.carFetcher.pojo.CarModel;

/**
 * @author fei
 * @Time：2017年4月13日 上午11:08:30
 * @version 1.0
 */
public class DbUtil {
	
	private static CarModelMapper carModelMapper;
	
	private static ApplicationContext context;
	
	static{
		context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext*.xml");
		carModelMapper = context.getBean(CarModelMapper.class);
	}

	public static synchronized CarModel getCarModelByPidAndName(String pid, String name) {
		List<CarModel> list = carModelMapper.selectCarModelByPidAndName(pid, name);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}

	public static synchronized void updateCarModel(CarModel carModel) {
		carModelMapper.updateByPrimaryKeySelective(carModel);
		
	}

	public static synchronized void insertCarModel(CarModel carModel) {
		carModelMapper.insertSelective(carModel);
	}
	
}
