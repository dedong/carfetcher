package com.fei.carFetcher.pojo;

import java.util.List;
import java.util.Map;

/**
 * @author fei
 * @Time：2017年4月6日 上午9:11:57
 * @version 1.0
 */
public class ParamItem {

	String name;
	
	List<Map<String, String>> valueitems;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Map<String, String>> getValueitems() {
		return valueitems;
	}

	public void setValueitems(List<Map<String, String>> valueitems) {
		this.valueitems = valueitems;
	}

	

}
