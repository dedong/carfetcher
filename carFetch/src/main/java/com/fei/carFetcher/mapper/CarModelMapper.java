package com.fei.carFetcher.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fei.carFetcher.pojo.CarModel;
import com.github.abel533.mapper.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface CarModelMapper {

	CarModel selectCarModelByPidAndName(@Param("pid") String pid, @Param("name") String name);

	void insertSelective(CarModel carModel);

	void updateByPrimaryKeySelective(CarModel carModel);

}