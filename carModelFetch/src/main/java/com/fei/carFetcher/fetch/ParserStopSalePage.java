package com.fei.carFetcher.fetch;

import com.fei.carFetcher.utils.HttpClientUtil;
import com.google.common.collect.Lists;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;



public class ParserStopSalePage {

    /**获取停售的车型配置
     * parseStopSaleData:(). 
     * @author fei 
     * @Time：2017年4月7日 下午2:53:04
     * @param url
     * @return
     * @throws IOException 
     */
    public List<String> parseStopSaleData(String url) throws IOException{
    	Document document = null;
    	List<String> lists = Lists.newArrayList();
		try {
			document = getDocument(url);
		} catch (Exception e) {
			e.printStackTrace();
			return lists;
		}
        //这部分用来获取参数配置
        Elements configElems = document.select(".models_nav");
        if(configElems.isEmpty())return lists;
        for (Element configElem : configElems) {
            String href = configElem.select("a").get(1).attr("href");
            String link = "http://www.autohome.com.cn/" + href;
            lists.add(link);
        }
    	
		return lists;
    	
    }
    
    public Document getDocument(String url) throws IOException {
        String content = HttpClientUtil.sendHttpsGet(url);
        Document document = Jsoup.parse(content);
        return document;
    }
    
}
