package com.arshiner;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.arshiner.common.ConfigManager;
import com.arshiner.common.SystemInfo;
@Component
public class OpenWy implements CommandLineRunner,Ordered{
	 private static Logger logger = Logger.getLogger(OpenWy.class);
	    @Value("${spring.web.loginurl}")
	    private String loginUrl;


	    @Value("${spring.auto.openurl}")
	    private boolean isOpen;

	    private volatile int count =0;
	  //配置文件
		ConfigManager config = ConfigManager.getInstance();
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		count++;
		System.out.println("count------"+count);
		System.out.println("OpenWy-----start");
		if(ConfigManager.properties.containsKey("peizhi")){
			if (ConfigManager.properties.get("peizhi").equals("")) {
				isOpen = true;
			}else{
				if (ConfigManager.properties.containsKey("fip")) {
					if (ConfigManager.properties.getProperty("fip").equals("")) {
						isOpen =true;
					}else{
						isOpen = false;
					}
				}else{
					isOpen = true;
				}
			}
		}
		 if(isOpen&&SystemInfo.getOS_Name().contains("Windows")){
			 
	            String cmd = "rundll32 url.dll,FileProtocolHandler "+ loginUrl;
	            Runtime run = Runtime.getRuntime();
	            try{
	                run.exec(cmd);
	                logger.debug("启动浏览器打开项目成功"+isOpen);
	            }catch (Exception e){
	                e.printStackTrace();
	                logger.error(e.getMessage());
	            }
	        }
	}


	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 1;
	}
}
