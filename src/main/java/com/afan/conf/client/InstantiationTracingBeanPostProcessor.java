package com.afan.conf.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
//保证spring容器加载完成后执行
public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {
	
	@Autowired
	AfanConfigMgr mgr;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		mgr.init();
	}
}
