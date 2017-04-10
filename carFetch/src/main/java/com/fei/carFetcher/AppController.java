package com.fei.carFetcher;
/**
 * @author fei
 * @Time：2017年4月6日 下午3:21:45
 * @version 1.0
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AppController extends SpringBootServletInitializer{
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(AppController.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AppController.class, args);
	}
}
