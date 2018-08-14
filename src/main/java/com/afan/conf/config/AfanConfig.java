package com.afan.conf.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

public class AfanConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String _v;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> _l;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> _m;
	
	public AfanConfig() {
	}
	
	public AfanConfig(String value) {
		this._v = value;
	}
	
	public String value() {
		return _v;
	}
	
	public String get_v() {
		return _v;
	}
	public void set_v(String _v) {
		this._v = _v;
	}
	public List<String> get_l() {
		return _l;
	}
	public void set_l(List<String> _l) {
		this._l = _l;
	}
	public Map<String, String> get_m() {
		return _m;
	}
	public void set_m(Map<String, String> _m) {
		this._m = _m;
	}
	
}