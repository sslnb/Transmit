package com.arshiner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.arshiner.common.Agent;
import com.arshiner.common.ConfigManager;
import com.arshiner.common.JDBCUtil;
import com.arshiner.common.JsonToObject;
import com.arshiner.common.ThreadPool;
import com.arshiner.nio.heartClient.HeartClient;
import com.arshiner.nio.transmitServer.TransmitServer;

/**
 * Agent 实现
 * 
 * @author ssl
 *
 */
@Component
public class StartAgent implements CommandLineRunner, Ordered {
	// 配置参数
	private static final Logger logger = Logger.getLogger(StartAgent.class);
	ConfigManager config = ConfigManager.getInstance();
	String fip = "";
	String kip = "";
	String port = "";
	String oraport = "";
	static String model = "";
	String peizhi = "";
	String sid = "";

	@Async
	@Override
	public void run(String... args) {
		new TransmitServer().start();
		peizhi = ConfigManager.properties.get("peizhi").toString();
		model = ConfigManager.properties.get("model").toString();
		fip = ConfigManager.properties.getProperty("fip");
		kip = ConfigManager.properties.getProperty("kip");
		port = ConfigManager.properties.getProperty("port");
		oraport = ConfigManager.properties.getProperty("oraport");
		sid = ConfigManager.properties.getProperty("sid");
		if (peizhi.equals("")) {
			return;
		}
		ThreadPool threadPool = new ThreadPool(2); // 创建一个此表剩余可连接的线程数目工作线程的线程池。
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					new HeartClient().connect(9091, ConfigManager.properties.getProperty("fip"));
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("等待heartserver连接");
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(timerTask, 10, 5000);
		// 任务一
		threadPool.execute(new Runnable() {

			@Override
			public void run() {
				// Agent传输
				run1();
			}
		});

		// 任务2刷session
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Map<String, Object> jm = flashpeizhi();
					JDBCUtil jdbc = new JDBCUtil("adtmgr", "adtmgr",
							ConfigManager.properties.getProperty("orafip"), oraport, "gzdora");
					try {
						for (Iterator<Entry<String, Object>> it = jm.entrySet().iterator(); it.hasNext();) {
							Entry<String, Object> entry = it.next();
							String selectdb = "select * from dbconpro where jgxtlb='" + entry.getKey() + "'";
							try {
								jdbc.getConnection();
							} catch (SQLException e1) {
								logger.info("连接失败");
								continue;
							}
							Map<String, String> map = jdbc.executeQuery(selectdb);
							flashsession(map, entry.getKey());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}finally {
						jdbc.closeDB();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		threadPool.waitFinish(); // 等待所有任务执行完毕
		threadPool.closePool(); // 关闭线程池

	}

	/**
	 * 刷新session
	 * 
	 * @param map
	 * @param jgxtlb
	 * @param jdbc
	 */
	public void flashsession(Map<String, String> map, String jgxtlb) {
		JDBCUtil db = new JDBCUtil(map.get("USERNAME"), map.get("PASSWORD"), map.get("IP"), map.get("PORT"),
				map.get("SID"));
		JDBCUtil jdbc = new JDBCUtil("adtmgr", "adtmgr", ConfigManager.properties.getProperty("orafip"), "1521",
				"gzdora");
		map = null;
		String sql = "select * from (select to_char(sample_time,'yyyy/mm/dd hh24:mi:ss') t, session_id sid, session_serial# serial, user_id as user#, program, machine"
				+ " from dba_hist_active_sess_history where user_id>0 " + "union "
				+ "select to_char(prev_exec_start,'yyyy/mm/dd hh24:mi:ss') t, sid, serial# as serial, user#, program, machine"
				+ " from v$session where user# > 0 " + "union "
				+ "select to_char(sample_time,'yyyy/mm/dd hh24:mi:ss') t, session_id sid, session_serial# serial, user_id, module, machine"
				+ " from v$active_session_history  where user_id>0" + " order by t desc) where rownum<500 ";
		String sql1 = "select * from ( select to_char(prev_exec_start,'yyyy/mm/dd hh24:mi:ss') t, sid, serial# as serial, user#, program, machine"+
"from v$session where user# > 0) order by t desc";
		try {
			jdbc.getConnection();
			db.getConnection();
			List<Map<String, Object>> listsessionmap = new ArrayList<>();
			listsessionmap = db.executeQuery(sql, null);
			for (Iterator<Map<String, Object>> iterator = listsessionmap.iterator(); iterator.hasNext();) {
				Map<String, Object> sessionMap = (Map<String, Object>) iterator.next();
				sql = "select * from Asession where " + "jgxtlb ='" + jgxtlb + "' and " + "sid ='"
						+ sessionMap.get("sid") + "' and " + "serial ='" + sessionMap.get("serial") + "' and " + "t ='"
						+ sessionMap.get("t") + "'";
				Map<String, String> sessionMap1 = new HashMap<>();
				sessionMap1 = jdbc.executeQuery(sql);
				if (!sessionMap.isEmpty()) {
					if (sessionMap.get("program").equals("")) {
						sessionMap.put("program", "JDBC Thin Client");
					}
					if (sessionMap1.isEmpty()) {
						sql = "insert into Asession (jgxtlb,t,sid,serial,user#,program,machine) values('" + jgxtlb
								+ "','" + sessionMap.get("t") + "','" + sessionMap.get("sid") + "','"
								+ sessionMap.get("serial") + "','" + sessionMap.get("user#") + "','"
								+ sessionMap.get("program") + "','" + sessionMap.get("machine") + "')";
						jdbc.executeUpdate(sql);
					} else {
						sql = "update Asession set " + "t ='" + sessionMap.get("t") + "'," + "sid ='"
								+ sessionMap.get("sid") + "'," + "serial ='" + sessionMap.get("serial") + "',"
								+ "user# ='" + sessionMap.get("user#") + "'," + "program ='" + sessionMap.get("program")
								+ "'," + "machine ='" + sessionMap.get("machine") + "' where " + "jgxtlb ='" + jgxtlb
								+ "' and " + "t ='" + sessionMap.get("t") + "' and " + "sid ='" + sessionMap.get("sid")
								+ "' and " + "serial ='" + sessionMap.get("serial") + "'";
						jdbc.executeUpdate(sql);
					}
				}
			}
			//select * from asession 
			//where to_date(t,'yyyy/mm/dd hh24:mi:ss')< systimestamp -interval'1'day order by t desc
		} catch (SQLException e) {
			logger.info(sql);
			e.printStackTrace();
		} finally {
			db.closeDB();
			jdbc.closeDB();
			db = null;
			jdbc = null;
		}

	}

	public void run1() {
		// 两个集合，待传集合，文件夹内文件信息
		// 将剩余连接数获取；
		Map<String, Object> jm = flashpeizhi();
		ThreadPool threadPool;
		while (true) {
			if (ConfigManager.properties.get("kip") != null) {
			} else {
				continue;
			}
			if (peizhi.equals("")) {
				continue;
			}
			threadPool = new ThreadPool(jm.size()); // 创建一个此表剩余可连接的线程数目工作线程的线程池。
			if (jm == null || jm.isEmpty()) {
				continue;
			}
			int i = 0;// 线程号
			JDBCUtil jdbc = new JDBCUtil("adtmgr", "adtmgr", ConfigManager.properties.getProperty("orafip"), "1521",
					"gzdora");
			try {
				jdbc.getConnection();
			} catch (SQLException e1) {
				continue;
			}
			try {
				for (Iterator<Entry<String, Object>> it = jm.entrySet().iterator(); it.hasNext();) {
					Entry<String, Object> entry = it.next();
					String selectdb = "select * from dbconpro where jgxtlb='" + entry.getKey() + "'";
					Map<String, String> map = jdbc.executeQuery(selectdb);
					if (!map.isEmpty()) {
						threadPool.execute(createTask(i, entry.getKey(), entry.getValue().toString(), map));
						i++;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				jdbc.closeDB();
			}
			
			threadPool.waitFinish(); // 等待所有任务执行完毕
			threadPool.closePool(); // 关闭线程池
		}
	}

	/**
	 * 刷新配置
	 */
	public static Map<String, Object> flashpeizhi() {
		String peizhi = ConfigManager.properties.get("peizhi").toString();
		if (peizhi.equals("")) {
			logger.info("StartAgent ----------- 无配置信息");
			return new HashMap<>();
		} else {
			return JsonToObject.JSONconsvertToMap(JsonToObject.StringconsvertToJSONObject(peizhi));
		}
	}

	/**
	 * 传输 主体功能
	 * 
	 * @param i
	 *            序号
	 * @param jgxtlb
	 *            交管系统类别全程code
	 * @param mulu
	 *            传输路径
	 * @return
	 */
	@Async
	private Runnable createTask(int i, String jgxtlb, String mulu, Map<String, String> map) {
		return new Runnable() {
			@Override
			public void run() {
				// 创建传输对象
				Agent agent = new Agent();
				agent.setStatus(true);
				agent.setFip(fip);
				agent.setPort(port);
				agent.setKip(kip);
				agent.setRedo(map);
				try {
					agent.runGD(jgxtlb, model);
					agent.runRedo(jgxtlb, model);
				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error(e);
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public int getOrder() {
		return 2;
	}

}
