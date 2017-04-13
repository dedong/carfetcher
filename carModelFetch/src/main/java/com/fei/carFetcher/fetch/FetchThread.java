package com.fei.carFetcher.fetch;

/**
 * @author fei
 * @Time：2017年4月13日 上午11:19:49
 * @version 1.0
 */
public class FetchThread implements Runnable{
	
	private String url;
	
	public FetchThread(String url) {
		this.url = url;
	}

	@Override
	public void run() {
		FetchBegin fetchBegin = new FetchBegin();
		fetchBegin.fetch(url);
	}
	
}
