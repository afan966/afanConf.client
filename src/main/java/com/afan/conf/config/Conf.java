package com.afan.conf.config;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afan.conf.client.AfanConfigMgr;

public class Conf {

	private static final Pattern map = Pattern.compile("(([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+))");
	private static final Pattern list = Pattern.compile("(([a-zA-Z0-9]+)\\[([\\d]+)\\])");

	private static final Map<String, AfanConfig> getMap() {
		return AfanConfigMgr.getConfigMgr().getConfigs();
	}

	public static int getInt(String key) {
		return getInt(key, 0);
	}

	public static double getDouble(String key) {
		return getDouble(key, 0);
	}

	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public static String getString(String key) {
		return getString(key, null);
	}

	public static int getInt(String key, int def) {
		try {
			return Integer.parseInt(getString(key));
		} catch (Exception e) {
		}
		return def;
	}

	public static double getDouble(String key, double def) {
		try {
			return Double.parseDouble(getString(key));
		} catch (Exception e) {
		}
		return def;
	}

	public static boolean getBoolean(String key, boolean def) {
		try {
			return Boolean.parseBoolean(getString(key));
		} catch (Exception e) {
		}
		return def;
	}

	public static String getString(String key, String def) {
		AfanConfig config = getMap().get(key);
		if (config != null) {
			return config.get_v();
		} else {
			Matcher m = map.matcher(key);
			if (m.find()) {
				String confKey = m.group(2);
				String confValue = m.group(3);
				if (confKey != null && confValue != null) {
					Map<String, String> data = getMap(confKey);
					if (data != null) {
						return data.get(confValue);
					}
				}
			}
			m = list.matcher(key);
			if (m.find()) {
				String confKey = m.group(2);
				String confIndex = m.group(3);
				if (confKey != null && confIndex != null) {
					int i = Integer.parseInt(confIndex);
					List<String> data = getList(confKey);
					if (data != null && data.size() > 0 && data.size() > Math.abs(i)) {
						if (i >= 0) {
							return data.get(i);
						} else {
							return data.get(data.size() + i);
						}
					}
				}
			}
		}
		return def;
	}

	public static List<String> getList(String key) {
		AfanConfig config = getMap().get(key);
		if (config != null) {
			return config.get_l();
		}
		return null;
	}

	public static Map<String, String> getMap(String key) {
		AfanConfig config = getMap().get(key);
		if (config != null) {
			return config.get_m();
		}
		return null;
	}

}
