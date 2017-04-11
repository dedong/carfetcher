package com.fei.carFetcher.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fei.carFetcher.mapper.CarModelMapper;
import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.pojo.CarModelVo;
import com.fei.carFetcher.service.CarModelService;
import com.fei.carFetcher.utils.HssfExportUtil;

/**
 * @author fei
 * @Time：2017年4月6日 下午3:59:16
 * @version 1.0
 */
@Service
public class CarModelServiceImpl implements CarModelService {
	
	@Autowired
	private CarModelMapper carModelMapper;
	
	@Override
	public CarModel getCarModelByPidAndName(String pid, String name) {
		CarModel selectOne = carModelMapper.selectCarModelByPidAndName(pid, name);
		return selectOne;
	}

	@Override
	public void updateCarModel(CarModel carModel) {
		carModelMapper.updateByPrimaryKeySelective(carModel);
		
	}

	@Override
	public void insertCarModel(CarModel carModel) {
		carModelMapper.insertSelective(carModel);
	}

	@Override
	public void exportCarModel() throws IOException {
		//List<CarModelVo> carModelList = carModelMapper.selectAllCarModel();
		//存储过程begin
		Map<String, Object> map = new HashMap<>();
		carModelMapper.queryCarModel(map);
		List<CarModelVo> carModelList = (List<CarModelVo>) map.get("result");
		//存储过程end
		if(carModelList!=null){
			HssfExportUtil.exportCarModel(carModelList);
		}
	}

	@Override
	public void otherOptionsDelete() {
		carModelMapper.otherOptionsDelete();
	}

	@Override
	public void test() {
//		Map<String, Object> m = new HashMap<>();
//		m.put("cid", "A913496D97214877A9CC4BDFF7281A24");
//		carModelMapper.queryByCondition(m);
//		System.out.println(m.get("name"));
		
		Map<String, Object> map = new HashMap<>();
		
		carModelMapper.queryCarModel(map);
		Integer count = (Integer) map.get("result");
		System.out.println(count);
	}
	
	
	


}
