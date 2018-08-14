package com.afan.conf.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.afan.conf.config.ConfigResponse;
import com.afan.tool.json.JsonUtil;
 
public class AfanConfigWebSocketClient extends WebSocketClient{
 
	public AfanConfigWebSocketClient(String url) throws URISyntaxException {
		super(new URI(url));
	}
 
	@Override
	public void onOpen(ServerHandshake shake) {
		System.out.println("onOpen");
		for(Iterator<String> it=shake.iterateHttpFields();it.hasNext();) {
			String key = it.next();
			System.out.println(key+":"+shake.getFieldValue(key));
		}
	}
 
	@Override
	public void onMessage(String paramString) {
		System.out.println("onMessage"+paramString);
		ConfigResponse response = JsonUtil.toObject(paramString, ConfigResponse.class);
		if(response==null){
			System.out.println("response null");
			return;
		}
		if("login".equals(response.getAction())){
			if(response.success()){
				AfanConfigMgr.getConfigMgr().loginSuccess(response);
			}else{
				System.out.println("response errror");
				AfanConfigMgr.getConfigMgr().loginFail(response);
			}
		}else{
			 if("AllConfig".equals(response.getAction())){
				 AfanConfigMgr.getConfigMgr().loadConf(response.getConfigMap());
			 }if("DeleteConfig".equals(response.getAction())){
				 AfanConfigMgr.getConfigMgr().deleteConf(response.getMessage());
			 }else{
				 AfanConfigMgr.getConfigMgr().upgradeConf(response.getConfigMap());
			 }
		}
	}
 
	@Override
	public void onClose(int paramInt, String paramString, boolean paramBoolean) {
		System.out.println("onClose...");
	}
 
	@Override
	public void onError(Exception e) {
		System.out.println("onError"+e);
		AfanConfigMgr.getConfigMgr().closeConf();
		e.printStackTrace();
	}
	
	public static void main(String[] args) {
		try {
			AfanConfigWebSocketClient client = new AfanConfigWebSocketClient("ws://localhost:8001/afanConf/test/tradeCenter");
			client.connect();
			Thread.sleep(1000);
			client.send("11111111");
			boolean flag = true;
			int i=10;
			while(flag) {
				client.send("aaabbbccc"+(i--));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(i == 0) {
					flag = false;
				}
			}
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

