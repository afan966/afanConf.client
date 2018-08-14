package com.afan.conf.client;

public interface AfanConfigListener {
	
	boolean isListener(String key);
	
	public void onAdd(String key, Object value);
	
	public void onUpdate(String key, Object oldValue, Object newValue);
	
	public void onDelete(String key);
	
}
