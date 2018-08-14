package com.afan.conf.client;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.afan.conf.config.AfanConfig;
import com.afan.conf.config.Conf;
import com.afan.conf.config.ConfValue;
import com.afan.conf.config.ConfigRequest;
import com.afan.conf.config.ConfigResponse;
import com.afan.tool.json.JsonUtil;

/**
 * 客户端配置管理器
 * @author afan
 *
 */
@Service
public class AfanConfigMgr {
	private static final Logger logger = LoggerFactory.getLogger(AfanConfigMgr.class);
	private static AfanConfigMgr mgr = null;

	private static AfanConfigWebSocketClient client = null;
	
	private static final Map<String, AfanConfig> configMap = new HashMap<String, AfanConfig>();

	@Value("${afanConfig.serviceUrl}")
	String configUrl = null;
	@Value("${app.name}")
	String appId = null;
	@Value("${app.instance}")
	String appInstance = null;
	
	@Autowired
	ApplicationContextUtil applicationContextUtil;
	
	private String token = null;
	
	private static final List<AfanConfigListener> listeners = new ArrayList<AfanConfigListener>();


	private AfanConfigMgr() {
	}

	//@PostConstruct//必须等待容器加载完成
	public void init(){
		mgr = AfanConfigMgr.getConfigMgr();
		mgr.configUrl = configUrl;
		mgr.appId = appId;
		mgr.appInstance = appInstance;
		mgr.connectConf();
	}
	
	public synchronized static AfanConfigMgr getConfigMgr() {
		if (mgr == null) {
			mgr = new AfanConfigMgr();
		}
		return mgr;
	}
	
	public void addListener(AfanConfigListener listener){
		listeners.add(listener);
	}
	
	public AfanConfigMgr connectConf(){
			if(!connect(0, 3)){
				//客户端本地缓存
				logger.warn("load local afan.conf<<<<");
			}
		return mgr;
	}
	
	private boolean connect(int i, int max) {
		try {
			if(i++>=max){
				return false;
			}
			client = new AfanConfigWebSocketClient("ws://" + configUrl + "/" + appId + "/" + appInstance);
			client.connect();
			Thread.sleep(1000);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			connect(i, max);
		}
		return false;
	}
	
	
	
	public void closeConf(){
		try {
			client.close();
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loginSuccess(ConfigResponse response){
		this.token = response.getMessage();
		ConfigRequest request = new ConfigRequest();
		request.setAction("AllConfig");
		request.setToken(token);
		InetAddress address = getLocalHostLANAddress();
		request.addAttr("ip", address.getHostAddress());
		this.sendRequest(request);
	}
	
	public void loginFail(ConfigResponse response){
		closeConf();
		connectConf();
	}
	
	public void loadConf(Map<String, AfanConfig> conf){
		for (String key : conf.keySet()) {
			AfanConfig oldConf = configMap.get(key);
			AfanConfig newConf = conf.get(key);
			if(listeners.size()>0){
				if(oldConf!=null){
					for (AfanConfigListener listener : listeners) {
						listener.onUpdate(key, oldConf, newConf);
					}
				}else{
					for (AfanConfigListener listener : listeners) {
						listener.onAdd(key, newConf);
					}
				}
			}
		}
		configMap.putAll(conf);
		
		//初始化注解参数
		initAnnotationValue();
	}
	
	public void upgradeConf(Map<String, AfanConfig> conf){
		configMap.putAll(conf);
	}
	
	public void deleteConf(String key){
		for (AfanConfigListener listener : listeners) {
			listener.onDelete(key);
		}
	}
	
	public void sendRequest(ConfigRequest request){
		client.send(JsonUtil.toJson(request));
	}
	
	public Map<String, AfanConfig> getConfigs(){
		return configMap;
	}
	
	private void initAnnotationValue(){
		ApplicationContext context = ApplicationContextUtil.getContext();
		if(context == null){
			return;
		}
		for (String beanName : context.getBeanDefinitionNames()) {
			if (!beanName.startsWith("org.springframework")){
				Object object = context.getBean(beanName);
				String cglibType = context.getType(beanName).getName();
				if(cglibType.indexOf("$$")>0){
					cglibType = cglibType.substring(0,cglibType.indexOf("$$"));
				}
				Field[] fields = null;
				try {
					fields = Class.forName(cglibType).getDeclaredFields();
				} catch (Exception e) {
				}
				if(fields==null || fields.length==0){
					continue;
				}
				for (Field field : fields) {
					ConfValue conf = field.getAnnotation(ConfValue.class);
					if(conf != null){
						Object value = null;
						if(int.class == field.getType()){
							value = Conf.getInt(conf.value());
						}else if(double.class == field.getType()){
							value = Conf.getDouble(conf.value());
						}else if(boolean.class == field.getType()){
							value = Conf.getBoolean(conf.value());
						}else{
							value = Conf.getString(conf.value());
						}
						if(value!=null){
							try {
								field.setAccessible(true);//改变私有属性
								field.set(object, value);
								logger.debug("ConfValue:{} vlaue:{} <<<<",field, value);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	public InetAddress getLocalHostLANAddress() {
	    try {
	        InetAddress candidateAddress = null;
	        // 遍历所有的网络接口
	        for (Enumeration<?> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // 在所有的接口下再遍历IP
	            for (Enumeration<?> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
	                    if (inetAddr.isSiteLocalAddress()) {
	                        // 如果是site-local地址，就是它了
	                        return inetAddr;
	                    } else if (candidateAddress == null) {
	                        // site-local类型的地址未被发现，先记录候选地址
	                        candidateAddress = inetAddr;
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            return candidateAddress;
	        }
	        // 如果没有发现 non-loopback地址.只能用最次选的方案
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        return jdkSuppliedAddress;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
