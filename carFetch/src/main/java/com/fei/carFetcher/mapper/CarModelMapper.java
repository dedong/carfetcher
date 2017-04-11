package com.fei.carFetcher.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.pojo.CarModelVo;

@org.apache.ibatis.annotations.Mapper
public interface CarModelMapper {

	CarModel selectCarModelByPidAndName(@Param("pid") String pid, @Param("name") String name);

	void insertSelective(CarModel carModel);

	void updateByPrimaryKeySelective(CarModel carModel);
	
	List<CarModelVo> selectAllCarModel();
	
	void otherOptionsDelete();
	
	String queryByCondition(Map<String, Object> m);

	void queryCarModel(Map<String, Object> map);
}