package com.arshiner;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:${user.dir}/application.properties") 
public class Application {
//启动类
	public static void main(String[] args) {
		
//		String peizhi = ConfigManager.properties.get("peizhi").toString();
//		if (peizhi.equals("")&&SystemInfo.getOS_Name().toLowerCase().startsWith("windows")) {
//			EventQueue.invokeLater(new Runnable() {
//				public void run() {
//					try {
//						TransmitConfig frame = new TransmitConfig();
//						frame.setVisible(true);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
		 SpringApplication sa = new SpringApplication(Application.class);
	     sa.run(args);
	}
	
}
