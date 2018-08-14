package com.afan.conf.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ConfigRequest {

	private String token;
	private String action;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> params;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void addAttr(String name, String value) {
		if(this.params == null){
			this.params = new HashMap<String, String>();
		}
		this.params.put(name, value);
	}
	
}
