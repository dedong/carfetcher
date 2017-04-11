package com.fei.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fei.carFetcher.mapper.CarModelMapper;
import com.fei.carFetcher.pojo.CarModel;

/**
 * @author fei
 * @Time：2017年4月11日 下午2:07:44
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/mybatis/SqlMapConfig.xml")
public class MapperTest extends AbstractJUnit4SpringContextTests {

	private CarModelMapper carModelMapper;

	@Before
	public void setup() {
		carModelMapper = applicationContext.getBean(CarModelMapper.class);
	}

	@Test
	public void test() {
		Map<String, Object> map = new HashMap<>();
		carModelMapper.queryCarModel(map);
		List<CarModel> lists = (List<CarModel>) map.get("result");
		System.out.println(lists.size());

	}

}
