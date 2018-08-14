package com.afan.conf.client;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext context = null;
	
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ApplicationContextUtil.context = arg0;
	}
	
	public static ApplicationContext getContext(){
		return context;
	}

}
