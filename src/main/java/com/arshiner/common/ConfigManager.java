package com.arshiner.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;
@Component
public class ConfigManager {
//	private static ConfigManager configManager ;
	public static  Properties properties= new Properties();
	private static boolean status=false;
	private static final String transmitPro=FilePathName.ROOT+"transmit.properties";
	private static ConfigManager config = new ConfigManager();
	
	// 静态工厂方法
    public static ConfigManager getInstance() {
    	if (config == null) {
    		config = new ConfigManager();
		}
		return config;
    }
	private ConfigManager(){
		try{ 
	        InputStream is = new BufferedInputStream (new FileInputStream(transmitPro));
				properties.load(is);
				System.out.println(transmitPro+"加载成功");
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		status=true;
	}
	/**
	 * 读写方法，读：configName和model必填，写全部必填
	 * @param confName
	 * @param model
	 * @param key
	 * @param value
	 * @return返回一个Atomic
	 */
	public synchronized boolean configGetAndSet(String key,String value) {
		boolean sign = false;
			 FileOutputStream oFile ;
			try{ 
				 oFile = new FileOutputStream(transmitPro,false);
				 properties.setProperty(key, value);
				 properties.store(oFile, "The New properties file");
		         oFile.close();
				System.out.println(transmitPro+"写成功");
				sign=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sign;
	}

	
	
	
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		ConfigManager.status = status;
	}

	
}
