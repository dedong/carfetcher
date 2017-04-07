package com.fei.carFetcher.utils;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author fei
 * @Time：2017年4月7日 下午5:34:07
 * @version 1.0
 */
public class PinyinUtil {

	/**
	 * 获取车辆名称首字母 getFirstStr:().
	 * 
	 * @author fei
	 * @Time：2017年4月7日 下午5:34:51
	 * @param carName
	 * @return
	 */
	public static String getFirstStr(String carName) {
		try {
			if (StringUtils.isNoneBlank(carName)) {
				String firstChar = carName.substring(0,1);
				// 英文名
				if (firstChar.matches("^[a-zA-Z]*")) {
					return firstChar.toUpperCase();
				}
				// 中文名
				if(firstChar.matches("[\\u4E00-\\u9FA5]+")){
					String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(firstChar.charAt(0));
					String pinyin = pinyins[0];
					return pinyin.substring(0,1).toUpperCase();
				}
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	public static void main(String[] args) {
		String firstStr = getFirstStr("众泰");
		System.out.println(firstStr);
	}
	
}
