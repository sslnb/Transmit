package com.arshiner.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import com.arshiner.nio.transmitClient.TransmitClient;

public class Agent {
	private static final Logger logger = Logger.getLogger(Agent.class);
	ConfigManager config = ConfigManager.getInstance();
	private boolean status;
	private String fip;
	private String port;
	private String kip;
	private String num;// 计数点
	private Map<String, String> map;// db信息
//	// 缓存中
//	public static LinkedHashMap<String, LinkedHashMap<String, FileStatus>> oldFileInfo = new LinkedHashMap<>();
//	public static LinkedHashMap<String, LinkedHashMap<String, FileStatus>> newFileInfo = new LinkedHashMap<>();
//
//	// 配置文件
//	/**
//	 * 遍历文件夹中的文件信息，并将其放入oldFileInfo
//	 */
//	@Async
//	public void flasholdFileInfo(String jgxtlb, String filepath) {
//		File file = new File(filepath);
//		if (!file.exists()) {
//			status = false;
//			logger.info(filepath + "不存在");
//			return;
//		}
//		File[] fs = file.listFiles();
//		Arrays.sort(fs, new Comparator<File>() {
//			public int compare(File f1, File f2) {
//				long diff = f1.lastModified() - f2.lastModified();
//				if (diff > 0)
//					return -1;
//				else if (diff == 0)
//					return 0;
//				else
//					return 1;
//			}
//
//			public boolean equals(Object obj) {
//				return true;
//			}
//		});
//
//		if (oldFileInfo.get(jgxtlb) == null || oldFileInfo.get(jgxtlb).isEmpty()) {
//			LinkedHashMap<String, FileStatus> old = new LinkedHashMap<>();
//			for (int i = fs.length - 1; i > -1; i--) {
//
//				FileStatus filestatus = new FileStatus();
//				logger.info(fs[i].getAbsolutePath());
//				filestatus.setFilename(fs[i].getAbsolutePath());
//				filestatus.setLastModifed(fs[i].lastModified());
//				filestatus.setPosition(0);
//				filestatus.setFilesize(fs[i].length());
//				old.put(fs[i].getAbsolutePath(), filestatus);
//			}
//			oldFileInfo.put(jgxtlb, old);
//			newFileInfo.put(jgxtlb, old);
//		} else {
//			LinkedHashMap<String, FileStatus> newFIle = new LinkedHashMap<>();
//			newFIle.putAll(newFileInfo.get(jgxtlb));
//			LinkedHashMap<String, FileStatus> old = new LinkedHashMap<>();
//			old.putAll(oldFileInfo.get(jgxtlb));
//			for (int i = fs.length - 1; i > -1; i--) {
//				FileStatus filestatus = new FileStatus();
//				filestatus.setFilename(fs[i].getAbsolutePath());
//				filestatus.setLastModifed(fs[i].lastModified());
//				filestatus.setFilesize(fs[i].length());
//				// oldFileinfo如果存再 则不进行赋值
//				if (old.containsKey(fs[i].getAbsolutePath())) {
//					// 进行比较他们的
//					FileStatus oldfilestatus = old.get(fs[i].getAbsolutePath());
//					// 此文件是否修改了，修改了则重传
//					if (filestatus.getFilesize() != oldfilestatus.getFilesize()) {
//						oldfilestatus.setFilesize(filestatus.getFilesize());
//						old.put(fs[i].getAbsolutePath(), oldfilestatus);
//						// 如果存再
//						if (!newFIle.containsKey(fs[i].getAbsolutePath())) {
//							newFIle.put(fs[i].getAbsolutePath(), filestatus);
//						}
//					}
//				} else {
//					old.put(fs[i].getAbsolutePath(), filestatus);
//					newFIle.put(fs[i].getAbsolutePath(), filestatus);
//				}
//			}
//			oldFileInfo.put(jgxtlb, old);
//			newFileInfo.put(jgxtlb, newFIle);
//		}
//	}
//
//	/**
//	 * transmit 方法
//	 * 
//	 * @param filepath
//	 * @return
//	 */
//	public boolean transmit(String jgxtlb, String model) {
//		if (!status) {
//			return false;
//		}
//		try {
//			if (newFileInfo.get(jgxtlb).isEmpty() || newFileInfo == null) {
//				this.status = false;
//				return true;
//			} else {
//				ClientDTO uploadFile = new ClientDTO();
//				LinkedHashMap<String, FileStatus> newFIle = new LinkedHashMap<>();
//				newFIle.putAll(newFileInfo.get(jgxtlb));
//				LinkedHashMap<String, FileStatus> old = new LinkedHashMap<>();
//				old.putAll(oldFileInfo.get(jgxtlb));
//				for (Iterator<Map.Entry<String, FileStatus>> it = newFIle.entrySet().iterator(); it.hasNext();) {
//					Entry<String, FileStatus> entry = it.next();
//					File file = new File(entry.getKey());
//					// 获取文件md_5值
//					FileInputStream fis = new FileInputStream(entry.getKey());
//					String file_md5 = DigestUtils.md5Hex(fis);
//					fis.close();
//					FileStatus filestatus = old.get(entry.getKey());
//					uploadFile.setFile_md5(file_md5);
//					uploadFile.setStarPos(0l);
//					uploadFile.setFile(file);
//					uploadFile.setFileClientName(file.getName());
//					uploadFile.setLength(file.length());
//					uploadFile.setClientLogo(jgxtlb);
//					uploadFile.setFilestatus("Modified");
//					uploadFile.setIp(kip);
//					try {
//						// 传输
//						new TransmitClient().connect(new Integer(port), fip, uploadFile, "Modified");
//					} catch (Exception e) {
//						logger.info("等待连接-----连接失败 -----" + fip + ":" + port);
//						logger.error(e);
//						return false;
//					}
//					old.put(entry.getKey(), filestatus);
//					it.remove();
//				}
//				oldFileInfo.put(jgxtlb, old);
//				newFileInfo.put(jgxtlb, newFIle);
//				/**
//				 * 更新Agent的status
//				 */
//				return true;
//			}
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			// log.debug(e1);
//			return false;
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			// log.debug(e1);
//			return false;
//		}
//	}

	/**
	 * GD 刷新归档
	 * 
	 * @param jgxtlb
	 * @param model
	 * @throws IOException 
	 */
	public void runGD(String jgxtlb, String model) throws IOException {
		try {
			num = ConfigManager.properties.get("num").toString();
		} catch (Exception e) {
			
		}
		JDBCUtil db = new JDBCUtil(map.get("USERNAME"), map.get("PASSWORD"), map.get("IP"), map.get("PORT"),
				map.get("SID"));
		String time="";
		if (null==num||num.equals("")) {
		}else{
			time ="where" + "	time > '" + num + "'";
		}
		Map<String, String> gdPath = null;
		try {
			db.getConnection();
			String redo = "select time,wjm from ( select to_char(next_time,'yyyy/MM/dd hh24:mi:ss') as time, name as wjm from"
			+" v$archived_log a where a.name is not null order by next_time asc ) "+time;
			gdPath = db.executeQueryRedoGD(redo);
		} catch (SQLException e1) {
			logger.error("查询报错"+e1);
			e1.printStackTrace();
		}finally{
			db.closeDB();
		}
		String userdir = FilePathName.ROOT + "log" + FilePathName.FileSepeartor;
		for (Iterator<Entry<String, String>> it = gdPath.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			String wjm = "";
			System.out.println("                归档文件：：--------00000--===----===---"+entry.getValue());
			if (model.equals("NORMAL")) {
				wjm = entry.getValue();
			} else if (model.equals("ASM")) {
				asmToNFS(entry.getValue(), userdir);
				wjm = userdir+entry.getValue().substring(entry.getValue().lastIndexOf(FilePathName.FileSepeartor),entry.getValue().length());
			}
			File file = new File(wjm);
			if (!file.exists()) {
				System.out.println("              归档文件：：---------1111111-无此文件===----===---"+wjm);
				continue;
			}
			System.out.println("               ---------wjm::------"+wjm);
			ClientDTO filestatus = new ClientDTO();
			filestatus.setFile_md5("log");
			filestatus.setStarPos(0l);
			filestatus.setFile(file);
			filestatus.setFileClientName(file.getName());
			filestatus.setLength(file.length());
			filestatus.setClientLogo(jgxtlb);
			filestatus.setFilestatus("Modified");
			filestatus.setIp(kip);
			try {
				System.out.println("  传输GD-----------文件传输----------");
				new TransmitClient().connect(new Integer(port), fip, filestatus, "Modified");
				config.configGetAndSet("num", num);
				num = entry.getKey();
			} catch (Exception e) {
				logger.info("GD等待连接-----");
			}finally{
				if (file.exists()) {
					file.delete();
				}
			}
		}

	}
	public static  Map<String, String> redoStatus = new HashMap<>();
	/**
	 * redo 刷新redo
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void runRedo(String jgxtlb, String model) throws IOException, SQLException {
		JDBCUtil db = new JDBCUtil(map.get("USERNAME"), map.get("PASSWORD"), map.get("IP"), map.get("PORT"),
				map.get("SID"));
		Map<String, String> redoPath = null;
		try {
			db.getConnection();
			String redo = "select member as wjm from v$logfile";
			redoPath = db.executeQueryRedo(redo);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		db.closeDB();
		String userdir = FilePathName.ROOT + "redo" + FilePathName.FileSepeartor;
		for (Iterator<Entry<String, String>> it = redoPath.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			String wjm = "";
			if (model.equals("NORMAL")) {
				wjm = entry.getValue();
			} else if (model.equals("ASM")) {
				//ASM转换文件系统
				asmToNFS(entry.getValue(), userdir);
				wjm = userdir+entry.getValue().substring(entry.getValue().lastIndexOf(FilePathName.FileSepeartor),entry.getValue().length());
			}
			File file = new File(wjm);
			if (!file.exists()) {
				System.out.println("555555555555555555REDO刷ASM未成功");
				continue;
			}
			FileInputStream fis = new FileInputStream(wjm);
			String new_md5= DigestUtils.md5Hex(fis);
			if (redoStatus.containsKey(entry.getValue())) {
				String old_md5= redoStatus.get(entry.getValue());
				if (old_md5.equals(new_md5)) {
					fis.close();
					logger.info("文件名"+entry.getValue()+":-------------111没变化！---不传输！");
					continue;
				}else{
					logger.info("文件名"+entry.getValue()+":-----------222变化！---更新redostatus！");
					redoStatus.put(entry.getValue(), new_md5);
				}
			}else{
				redoStatus.put(entry.getValue(), new_md5);
			}
			fis.close();
			fis=null;
			ClientDTO filestatus = new ClientDTO();
			filestatus.setFile_md5("redo");
			filestatus.setStarPos(0l);
			filestatus.setFile(file);
			filestatus.setFileClientName(file.getName());
			filestatus.setLength(file.length());
			filestatus.setClientLogo(jgxtlb);
			filestatus.setFilestatus("Modified");
			filestatus.setIp(kip);
			try {
				logger.info("文件名"+entry.getValue()+":-----------3333变化！---传输！");
				new TransmitClient().connect(new Integer(port), fip, filestatus, "Modified");
			} catch (Exception e) {
				logger.info("REDO等待连接-----");
			}
		}
		JDBCUtil jdbc = new JDBCUtil("adtmgr", "adtmgr",ConfigManager.properties.getProperty("orafip"), ConfigManager.properties.getProperty("oraport"),ConfigManager.properties.getProperty("sid"));
		jdbc.getConnection();
		String stoptime = "select stoptime from acs where jgxtlb='"+jgxtlb+"'";
		Map<String, String> stoptimemap = jdbc.executeQueryNormal(stoptime);
		jdbc.closeDB();
		try {
			Thread.sleep(Long.valueOf(stoptimemap.get("stoptime").toString()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ASM转换
	 * @param oldwjm
	 * @param newwjm
	 * @throws IOException 
	 */
	public void asmToNFS (String oldwjm,String dir) throws IOException{
		String cmd ="asmcmd -p cp "+oldwjm+"  "+dir;
		Process proc =null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();
			System.out.println("1111111111111111111    "+cmd+"      11111111111111111111asmTONFS--------");
			int data =0;
			InputStream  isInput = proc.getInputStream();
			InputStream  errInput = proc.getErrorStream();
			while ((data = isInput.read())!=-1) {
				System.out.print((byte)data);
			}
			data=0;
			System.out.println();
			while ((data = errInput.read())!=-1) {
				System.out.print((byte)data);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("asm转文件系统：asmToNFS"+e);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("asm转文件系统 waitFor报错：asmToNFS"+e);
		}finally{
			if (proc!=null) {
				System.out.println("proc不为空，关闭流，强制关闭子进程");
				close(proc.getOutputStream());
				close(proc.getInputStream());
				close(proc.getErrorStream());
				proc.destroyForcibly();
				System.out.println("流关闭了！！！");
			}
		}
	}
	
	public void close(Closeable c){
		if (c!=null) {
			try{
				c.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public Map<String, String> getRedo() {
		return map;
	}

	public void setRedo(Map<String, String> redo) {
		this.map = redo;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getFip() {
		return fip;
	}

	public void setFip(String fip) {
		this.fip = fip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getKip() {
		return kip;
	}

	public void setKip(String kip) {
		this.kip = kip;
	}

}
