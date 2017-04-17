package com.fei.carFetcher.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.pojo.CarModelVo;

public interface CarModelMapper {

	List<CarModel> selectCarModelByPidAndName(@Param("pid") String pid, @Param("name") String name);

	void insertSelective(CarModel carModel);

	void updateByPrimaryKeySelective(CarModel carModel);
	
	List<CarModelVo> selectAllCarModel();

}