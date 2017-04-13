package com.fei.carFeicher;

import static org.junit.Assert.*;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fei.carFetcher.mapper.CarModelMapper;
import com.fei.carFetcher.pojo.CarModel;

/**
 * @author fei
 * @Time：2017年4月13日 上午10:02:01
 * @version 1.0
 */
public class MapperTest {
	
	private CarModelMapper carModelMapper;

	@Before
	public void setUp() throws Exception {
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("classpath:spring/applicationContext*.xml");
		
		SqlSessionFactory sessionFactory = (SqlSessionFactory) context.getBean("sqlSessionFactory");
		SqlSession session = sessionFactory.openSession(true);
		carModelMapper = session.getMapper(CarModelMapper.class);
		
	}

	@Test
	public void test() {
		CarModel carModel = carModelMapper.selectCarModelByPidAndName("4422EA4C30606B2AE050A8C0340118E8", "斯巴鲁");
		System.out.println(carModel);
	}

}
