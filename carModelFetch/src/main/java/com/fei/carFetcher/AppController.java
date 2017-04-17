package com.fei.carFetcher;

import java.util.Collections;
import java.util.List;import org.aspectj.weaver.NewConstructorTypeMunger;

import com.fei.carFetcher.fetch.FetchThread;
import com.google.common.collect.Lists;

/**
 * @author fei
 * @Time：2017年4月13日 上午9:56:29
 * @version 1.0
 * 程序入口
 */
public class AppController {

	public static void main(String[] args) {
		List<String> urls = Lists.newArrayList();
        for(char i = 65; i < 91;i++){
            String url = "http://www.autohome.com.cn/grade/carhtml/" + i + ".html";
            urls.add(url);
        }
        urls.remove("http://www.autohome.com.cn/grade/carhtml/E.html");
        urls.remove("http://www.autohome.com.cn/grade/carhtml/I.html");
        urls.remove("http://www.autohome.com.cn/grade/carhtml/U.html");
        urls.remove("http://www.autohome.com.cn/grade/carhtml/V.html");
        Collections.sort(urls);
        
      /*  for (String url : urls) {
			Thread thread = new Thread(new FetchThread(url));
			thread.start();
		}*/

        Thread thread = new Thread(new FetchThread("http://www.autohome.com.cn/grade/carhtml/A.html"));
		thread.start();
	}

}
