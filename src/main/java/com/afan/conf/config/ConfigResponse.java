package com.afan.conf.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ConfigResponse {

	private int status = 0;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, AfanConfig> configMap;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String action;
	
	public ConfigResponse() {
	}
	
	public ConfigResponse(String action) {
		this.action = action;
	}
	
	public boolean success(){
		return status == 0;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, AfanConfig> getConfigMap() {
		return configMap;
	}
	public void setConfigMap(Map<String, AfanConfig> configMap) {
		this.configMap = configMap;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
}
