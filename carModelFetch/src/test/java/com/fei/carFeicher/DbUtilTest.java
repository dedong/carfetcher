package com.fei.carFeicher;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.fei.carFetcher.pojo.CarModel;
import com.fei.carFetcher.utils.DbUtil;

/**
 * @author fei
 * @Time：2017年4月13日 上午11:14:31
 * @version 1.0
 */
public class DbUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		DbUtil dbUtil = new DbUtil();
		
		CarModel model = dbUtil.getCarModelByPidAndName("4422EA4C30606B2AE050A8C0340118E8", "斯巴鲁");
		System.out.println(model);
	}

}
