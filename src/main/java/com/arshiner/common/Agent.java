package com.arshiner.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.arshiner.nio.transmitClient.FileUploadClientHandler;
import com.arshiner.nio.transmitClient.TransmitClient;

/**
 * lsof -p 17228 |wc -l 服务端在客户端连接成功后异常断线 是什么情况
 * 
 * @author MSI-PC
 *
 */
public class Agent {
	private static final Logger logger = Logger.getLogger(Agent.class);
	ConfigManager config = ConfigManager.getInstance();
	private boolean status;
	private String fip;
	private String port;
	private String kip;
	private String num;// 计数点
	private Map<String, String> map;// db信息
	JDBCUtil jdbc; // jdbc ora服务端的
	JDBCUtil db;
	String maxthread = "";

	/**
	 * 构造出一个JDBC便于连接
	 * 
	 * @throws SQLException
	 */

	/**
	 * GD 刷新归档
	 * 
	 * @param jgxtlb
	 * @param model
	 * @throws IOException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void runGD(String jgxtlb, String model) throws IOException, SQLException, InterruptedException {
		jdbc.getConnection();
		String archivetime = "select archivetime from acs where jgxtlb='" + jgxtlb + "'";
		Map<String, String> archivetimemap = jdbc.executeQueryNormal(archivetime);
		jdbc.closeDB();
		if (!archivetimemap.isEmpty()) {
			num = archivetimemap.get("archivetime");
		}
		if (num.equals("") || null == num) {
			try {
				num = ConfigManager.properties.get("num").toString();
			} catch (Exception e) {
				return ;
			}
		}
		gdStatus = JsonToObject.JSONconsvertToMap(JsonToObject.StringconsvertToJSONObject(num));
		LinkedHashMap<String, String> gdPath = null;
		for (int i = 1; i <= Integer.valueOf(maxthread); i++) {
			String time = "";
			if (!gdStatus.containsKey("thread" + i)) {
			} else {
				time = "where" + "	time >= '" + gdStatus.get("thread" + i) + "'";
			}
			String redo = "";
			try {
				db.getConnection();
				redo = "select time,wjm from ( select sequence# as time, name as wjm from"
						+ " v$archived_log a where a.name is not null and thread#='" + i + "' order by sequence# asc )"
						+ time;
				gdPath = db.executeQueryRedoGD(redo);
			} catch (SQLException e1) {
				logger.error("查询报错" + e1);
				logger.error("sql::" + redo);
				e1.printStackTrace();
				break;
			} finally {
				db.closeDB();
			}
			String userdir = FilePathName.ROOT + "log" + FilePathName.FileSepeartor;
			for (Iterator<Entry<String, String>> it = gdPath.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				String wjm = "";
				if (model.equals("NORMAL")) {
					wjm = entry.getValue();
				} else if (model.equals("ASM")) {
					asmToNFS(entry.getValue(), userdir);
					wjm = userdir + entry.getValue().substring(entry.getValue().lastIndexOf(FilePathName.FileSepeartor),
							entry.getValue().length());
				}
				File file = new File(wjm);
				if (!file.exists()) {
					break;
				}
				ClientDTO filestatus = new ClientDTO();
				filestatus.setFile_md5("log");
				filestatus.setStarPos(0l);
				filestatus.setFile(file);
				filestatus.setFileClientName(file.getName());
				filestatus.setLength(file.length());
				filestatus.setClientLogo(jgxtlb);
				filestatus.setFilestatus("Modified");
				filestatus.setIp("thread" + i);
				try {
					TransmitClient tran = new TransmitClient();
					tran.connect(new Integer(port), fip, filestatus, "Modified");
					if (!FileUploadClientHandler.status) {
						break;
					} else {
						gdStatus.put("thread" + i, entry.getKey());
					}
					tran = null;
				} catch (Exception e) {
					logger.info("GD等待连接-----");
					break;
				} finally {
					if (file.exists()) {
						file.delete();
					}
				}
			}
			num = JsonToObject.MapconsvertToJson(gdStatus).toJSONString();
			config.configGetAndSet("num", num);
		} // for maxthread

	}

	public static Map<String, String> redoStatus = new HashMap<>();
	public static Map<String, Object> gdStatus = new HashMap<>();

	/**
	 * redo 刷新redo
	 * 
	 * @throws IOException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void runRedo(String jgxtlb, String model) throws IOException, SQLException, InterruptedException {
		List<String> redoPath = null;
		for (int i = 1; i <= Integer.valueOf(maxthread); i++) {
			try {
				db.getConnection();
				String redo = "select member as wjm from v$logfile where group# in (select group# from v$log where thread# = '"
						+ i + "')";
				redoPath = db.executeQueryRedo(redo);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			db.closeDB();
			String userdir = FilePathName.ROOT + "redo" + FilePathName.FileSepeartor;
			int i1 = 0;
			for (Iterator<String> it = redoPath.iterator(); it.hasNext();) {
				i1++;
				String entry = it.next();
				String wjm = "";
				if (model.equals("NORMAL")) {
					wjm = entry;
				} else if (model.equals("ASM")) {
					// ASM转换文件系统
					asmToNFS(entry,
							userdir + entry.substring(entry.lastIndexOf(FilePathName.FileSepeartor) + 1, entry.length())
									+ "_" + i1);
					wjm = userdir + entry.substring(entry.lastIndexOf(FilePathName.FileSepeartor) + 1, entry.length())
							+ "_" + i1;
				}
				File file = new File(wjm);
				if (!file.exists()) {
					break;
				}
				FileInputStream fis = new FileInputStream(wjm);
				String new_md5 = DigestUtils.md5Hex(fis);
				if (redoStatus.containsKey(entry)) {
					String old_md5 = redoStatus.get(entry);
					if (old_md5.equals(new_md5)) {
						fis.close();
						logger.error(wjm + "文件名md5相同");
						if (file.exists()) {
							file.delete();
						}
						continue;
					} else {
						redoStatus.put(entry, new_md5);
					}
				} else {
					redoStatus.put(entry, new_md5);
				}
				fis.close();
				fis = null;
				ClientDTO filestatus = new ClientDTO();
				filestatus.setFile_md5("redo");
				filestatus.setStarPos(0l);
				filestatus.setFile(file);
				filestatus.setFileClientName(file.getName());
				filestatus.setLength(file.length());
				filestatus.setClientLogo(jgxtlb);
				filestatus.setFilestatus("Modified");
				filestatus.setIp("thread" + i);
				try {
					logger.error("当前thread：" + i + "   当前redo文件名" + wjm);
					TransmitClient tran = new TransmitClient();
					tran.connect(new Integer(port), fip, filestatus, "Modified");
					if (!FileUploadClientHandler.status) {
						break;
					}
					tran = null;
				} catch (Exception e) {
					logger.info("REDO等待连接-----");
					break;
				}
				if (file.exists()) {
					file.delete();
				}
			}
		} // for maxthread

		jdbc.getConnection();
		String stoptime = "select stoptime from acs where jgxtlb='" + jgxtlb + "'";
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
		// jdbc.getConnection();
		// String isRerun = "select rerun from acs where jgxtlb='" + jgxtlb +
		// "'";
		// Map<String, String> isRerunmap = jdbc.executeQueryNormal(isRerun);
		// jdbc.closeDB();
		// if (isRerunmap.get("rerun").equals("1")) {
		// //重启Agent，调用脚本
		//
		// }
	}

	/**
	 * ASM转换
	 * 
	 * @param oldwjm
	 * @param newwjm
	 * @throws IOException
	 */
	public void asmToNFS(String oldwjm, String dir) throws IOException {
		String cmd = "su - grid  asmcmd -p cp " + oldwjm + "  " + dir;
		logger.error(cmd);
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();
			int data = 0;
			InputStream isInput = proc.getInputStream();
			InputStream errInput = proc.getErrorStream();
			while ((data = isInput.read()) != -1) {
			}
			data = 0;
			while ((data = errInput.read()) != -1) {
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("asm转文件系统：asmToNFS" + e);

		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("asm转文件系统 waitFor报错：asmToNFS" + e);
		} finally {
			if (proc != null) {
				System.out.println("proc不为空，关闭流，强制关闭子进程");
				close(proc.getOutputStream());
				close(proc.getInputStream());
				close(proc.getErrorStream());
				proc.destroyForcibly();
				System.out.println("流关闭了！！！");
			}
		}
	}

	// /**
	// * 重启
	// * @throws IOException
	// */
	// public void resumAgent() throws IOException{
	// String cmd = "/bin/sh " + FilePathName.ROOT+"resum.sh";
	// Process proc = null;
	// try {
	// proc = Runtime.getRuntime().exec(cmd);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	public void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, String> getRedo() {
		return map;
	}

	public void setRedo(Map<String, String> redo) throws SQLException {
		this.map = redo;
		jdbc = new JDBCUtil("adtmgr", "adtmgr", ConfigManager.properties.getProperty("orafip"),
				ConfigManager.properties.getProperty("oraport"), ConfigManager.properties.getProperty("sid"));
		db = new JDBCUtil(map.get("USERNAME"), map.get("PASSWORD"), map.get("IP"), map.get("PORT"), map.get("SID"));
		db.getConnection();
		String thread = "select max(thread#) as  maxthread from v$log";
		Map<String, String> threadmap = db.executeQueryNormal(thread);
		db.closeDB();
		maxthread = threadmap.get("maxthread");
		if (maxthread.equals("")) {
			maxthread = "1";
		}
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
