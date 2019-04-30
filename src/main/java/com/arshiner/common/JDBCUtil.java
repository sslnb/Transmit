package com.arshiner.common;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * JDBC工具类
 * 
 * @author 士林
 *
 */
public class JDBCUtil {

	// 表示定义数据库的用户名
	private String USERNAME;// = "root";
	// 定义数据库的密码
	private String PASSWORD;// = "root";
	// 定义数据库的驱动信息
	private String DRIVER = "oracle.jdbc.OracleDriver";// =
	// 定义访问数据库的地址
	private String ip;
	private String port;
	private String SID;
	private String ServiceName;
	private String tb_name;
	private String timeFied;
	private String date;// sjcq
	private String sjcz;
	private String where;
	// schema的值
	private String schema;
	// 定义数据库的链接
	private Connection con = null;
	// 定义sql语句的执行对象
	public PreparedStatement pstmt = null;
	// 定义查询返回的结果集合
	public ResultSet resultSet = null;
	public Statement stmt;

	private int pre = 0;
	private int sux = 0;
	private int count = 0;
	private int lastcount = 0;

	private int start = 0;
	private int end = 0;

	/**
	 * 获取存量的root子节点
	 * 
	 * @return
	 */
	public TreeMap<String, Object> getCLRootChild(String xtlb, String azdm) {
		TreeMap<String, Object> rootchild = new TreeMap<>();
		rootchild.put("ora_type", 1);
		rootchild.put("ora_azdm", azdm);
		rootchild.put("ora_xtlb", xtlb);
		rootchild.put("ora_user", this.USERNAME);
		// 获取数据抽取时间，即当前时间
		Date d = new Date();// 获取时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");// 转换格式
		rootchild.put("ora_client", "jdbc");
		rootchild.put("ora_time", sdf.format(d));
		rootchild.put("ora_schema", schema);
		rootchild.put("tb_name", tb_name);
		return rootchild;
	}

	/**
	 * 回去之后整改一下sql拼接 获取sql
	 * 
	 * @param com.arshiner.quartz.model
	 * @return
	 */
	public String getSql(int model) {
		String sql = "";

		if (model == 1) {
			if (timeFied == null || timeFied.equals("") || date.equals("") || date == null || sjcz.equals("")
					|| sjcz == null) {
				if (where != null && !where.equals("")) {// where不为空也不等于空字符串
					sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN FROM " + schema + "." + tb_name
							+ " A where  " + where + ") where RN <= " + sux + " ) " + " WHERE RN >" + pre;
					System.out.println(sql);
					return sql;
				} else {
					sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN FROM " + schema + "." + tb_name
							+ " A ) where RN <= " + sux + " ) " + " WHERE RN >" + pre;
					System.out.println(sql);
					return sql;
				}
			} else {// 这里是date不为空的时候，也就是说date日期参数不为空
				if (timeFied.equals("rowid")) {
					if (where != null && !where.equals("")) {// where不为空也不等于空字符串
						if (sjcz.equals("") || sjcz == null) {
							sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN ,ROWID as " + timeFied
									+ " FROM " + schema + "." + tb_name + " A where  " + where + " AND " + timeFied
									+ ">=" + date + ") where RN <= " + sux + " ) " + " WHERE RN >" + pre;
							System.out.println(sql);
							return sql;
						} else {
							sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN ,ROWID as " + timeFied
									+ " FROM " + schema + "." + tb_name + " A where  " + where + " AND " + timeFied
									+ ">=" + date + "AND " + timeFied + "<=" + sjcz + ") where RN <= " + sux + " ) "
									+ " WHERE RN >" + pre;
							System.out.println(sql);
							return sql;
						}
					} else {
						if (sjcz.equals("") || sjcz == null) {
							sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN ,ROWID as " + timeFied
									+ " FROM " + schema + "." + tb_name + " A where  " + timeFied + ">=" + date
									+ ") where RN <= " + sux + " ) " + " WHERE RN >" + pre;
							System.out.println(sql);
							return sql;
						} else {
							sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN,ROWID as " + timeFied
									+ " FROM " + schema + "." + tb_name + " A where " + timeFied + ">=" + date + "AND "
									+ timeFied + "<=" + sjcz + ") where RN <= " + sux + " ) " + " WHERE RN >" + pre;
							System.out.println(sql);
							return sql;
						}
					}
				}
				if (where != null && !where.equals("")) {// where不为空也不等于空字符串
					sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN FROM " + schema + "." + tb_name
							+ " A where  " + where + " and " + timeFied + "<= to_date(" + date
							+ ",'yyyy-MM-dd') ) where RN <= " + sux + " ) " + " WHERE RN >= " + pre;
					System.out.println(sql);
					return sql;
				} else {
					sql = "SELECT * FROM " + "(SELECT * FROM (SELECT A.* ,ROWNUM as RN FROM " + schema + "." + tb_name
							+ " A where  " + timeFied + "<= to_date(" + date + ",'yyyy-MM-dd') ) where RN <= " + sux
							+ " ) " + " WHERE RN >= " + pre;
					System.out.println(sql);
					return sql;
				}
			}
		} else if (model == 0) {
			// 如果model是0，则获取的是countSql
			if (timeFied == null || timeFied.equals("") || date.equals("") || date == null) {
				if (where != null && !where.equals("")) {// where不为空也不等于空字符串
					sql = "SELECT count(*) as c  FROM " + schema + "." + tb_name + " where" + where;
					System.out.println(sql);
					return sql;
				} else {
					sql = "SELECT count(*) as c  FROM " + schema + "." + tb_name;
					System.out.println(sql);
					return sql;
				}
			} else {
				if (timeFied.equals("rowid")) {
					if (where != null && !where.equals("")) {// where不为空也不等于空字符串
						sql = "SELECT  count(*) as c  ,ROWNUM as RN ,ROWID as " + timeFied + " FROM " + schema + "."
								+ tb_name + " A where  " + where + " AND " + timeFied + ">=" + date + "AND " + timeFied
								+ "<=" + sjcz;
						System.out.println(sql);
						return sql;
					} else {
						sql = "SELECT  count(*) as c ,ROWNUM as RN,ROWID as " + timeFied + " FROM " + schema + "."
								+ tb_name + " A where " + timeFied + ">=" + date + "AND " + timeFied + "<=" + sjcz;
						System.out.println(sql);
						return sql;
					}
				}
				if (where != null && !where.equals("")) {// where不为空也不等于空字符串
					sql = "SELECT count(*) as c  FROM " + schema + "." + tb_name + "where " + timeFied + ">=" + date
							+ " and " + where;
					System.out.println(sql);
					return sql;
				} else {
					sql = "SELECT count(*) as c  FROM " + schema + "." + tb_name + "where " + timeFied + ">=" + date;
					System.out.println(sql);
					return sql;
				}

			}
		}
		return sql;
	}

	/**
	 * 优化sql拼接
	 * 
	 * @return
	 */
	public String getsql() {
		StringBuilder sql = new StringBuilder();
		return sql.toString();

	}

	/**
	 * 获取需要查询出多少条数据，如果条数超过100000
	 * 
	 * @return
	 */
	public int getCount() {
		String sql = getSql(0);
		int size = 0;
		try {
			resultSet = getResultSet(sql);
			while (resultSet.next()) {
				size = resultSet.getInt("c");
			}
		} catch (SQLException e) {
			System.out.println("执行sql：" + sql + " 失败！");
		} finally {
			resultSet = null;
		}
		return size;
	}

	public void setPreAndSux1() {
		int size = end - pre;
		System.out.println("size============" + size);
		if (size > 0 && count == 0) {
			if (size > 1000 && size < 10000) {
				count++;
				lastcount = size / 10;
				sux = pre + lastcount;
			} else if (size < 1000) {
				count++;
				lastcount = size / 10;
				sux = pre + lastcount;
			} else if (size > 10000 && size < 100000) {
				count++;
				lastcount = (size / 100);
				sux = pre + lastcount;
			} else if (size > 100000 && size < 1000000) {
				count++;
				lastcount = (size / 1000);
				sux = pre + lastcount;
			} else {
				count++;
				lastcount = (size / 10000);
				sux = pre + lastcount;
			}
		} else {
			count++;
			System.out.println("count========" + count + "      lastcount=========" + lastcount);
			if (sux + lastcount <= end) {
				pre = sux;
				sux = sux + lastcount;
			} else {
				pre = sux;
				sux = end;
			}

		}
	}

	public void setPreAndSux(int size) {
		System.out.println("size============" + size);
		if (size > 0 && count == 0) {
			if (size > 1000 && size < 10000) {
				count++;
				lastcount = size / 10;
				pre = start;
				sux = lastcount;
			} else if (size < 1000) {
				count++;
				lastcount = size / 10;
				pre = start;
				sux = lastcount;
			} else if (size > 10000 && size < 100000) {
				count++;
				lastcount = (size / 100);
				pre = start;
				sux = lastcount;
			} else if (size > 100000 && size < 1000000) {
				count++;
				lastcount = (size / 1000);
				pre = start;
				sux = lastcount;
			} else {
				count++;
				lastcount = (size / 10000);
				pre = start;
				sux = lastcount;
			}
		} else {
			count++;
			System.out.println("count========" + count + "      lastcount=========" + lastcount);
			if (sux + lastcount <= size) {
				pre = sux;
				sux = sux + lastcount;
			} else {
				pre = sux;
				sux = size;
			}

		}
	}

	/**
	 * 无参构造
	 */
	public JDBCUtil() {

	}

	public JDBCUtil(String USERNAME, String PASSWORD, String ip, String port, String SID) {
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
		this.ip = ip;
		this.port = port;
		this.SID = SID;
	}

	/**
	 * 有参构造
	 * 
	 * @param USERNAME
	 * @param PASSWORD
	 * @param ip
	 * @param port
	 * @param SID
	 * @param schema
	 * @param ServiceName
	 * @param tb_name
	 * @param timeFied
	 * @param date
	 * @param where
	 */
	public JDBCUtil(String USERNAME, String PASSWORD, String ip, String port, String SID, String schema,
			String ServiceName, String tb_name, String timeFied, String date, String where) {
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
		this.ip = ip;
		this.port = port;
		this.SID = SID;
		this.schema = schema;
		this.ServiceName = ServiceName;
		this.tb_name = tb_name;
		this.timeFied = timeFied;
		this.date = date;
		this.where = where;
	}

	public JDBCUtil(String USERNAME, String PASSWORD, String ip, String port, String SID, String schema,
			String ServiceName, String tb_name, String where) {
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
		this.ip = ip;
		this.port = port;
		this.SID = SID;
		this.schema = schema;
		this.ServiceName = ServiceName;
		this.tb_name = tb_name;
		this.where = where;
	}

	public JDBCUtil(String USERNAME, String PASSWORD, String ip, String port, String SID, String schema,
			String ServiceName, String tb_name) {
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
		this.ip = ip;
		this.port = port;
		this.SID = SID;
		this.schema = schema;
		this.ServiceName = ServiceName;
		this.tb_name = tb_name;
	}

	/**
	 * 获取Map集合，String是列名，Object是值
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getresultMap(ResultSet rs) throws SQLException {
		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		if (rs.next()) {
			ResultSetMetaData rsMeta = rs.getMetaData();
			int columnCount = rsMeta.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				dataMap.put(rsMeta.getColumnLabel(i), rs.getObject(i));
			}
		}
		return dataMap;
	}

	/**
	 * 
	 * @param USERNAME
	 * @param PASSWORD
	 * @param ip
	 * @param port
	 * @param SID
	 * @param schema
	 * @param ServiceName
	 * @param tb_name
	 * @param timeFied
	 * @param date
	 * @param where
	 * @return
	 */
	public static JDBCUtil getInstance(String USERNAME, String PASSWORD, String ip, String port, String SID,
			String schema, String ServiceName, String tb_name, String timeFied, String date, String where) {
		JDBCUtil jdbc = null;
		if (timeFied == null || timeFied.equals("") || where.equals("") || where == null) {
			jdbc = new JDBCUtil(USERNAME, PASSWORD, ip, port, SID, schema, ServiceName, tb_name);
		} else {
			jdbc = new JDBCUtil(USERNAME, PASSWORD, ip, port, SID, schema, ServiceName, tb_name, timeFied, date, where);
		}

		return jdbc;
	}

	/**
	 * 从数据库中查询数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getResultSet(String sql, List<Object> params) throws SQLException {
		int index = 1;
		pstmt = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		return resultSet;

	}

	public ResultSet getResultSet(String sql) throws SQLException {
		stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		resultSet = stmt.executeQuery(sql);
		return resultSet;

	}

	/**
	 * //注册驱动
	 */
	private void registeredDriver() {
		try {
			Class.forName(DRIVER);
			System.out.println("注册驱动成功！");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 驱动注册和连接
	 * 
	 * @throws SQLException
	 */
	public void getConnection() throws SQLException {
		// 注册驱动
		registeredDriver();
		String URL = "jdbc:oracle:thin:@" + ip + ":" + port + "/" + SID;
		System.out.println(URL);
		con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}

	public static void main(String[] args) {
		// clResultSet();
		// 将剩余连接数获取；
		// long startTime = System.currentTimeMillis();
		// ThreadPool threadPool = new ThreadPool(2); //
		// 创建一个此表剩余可连接的线程数目工作线程的线程池。
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// // 线程池分好了
		// int start = 0;
		// int end = 0;
		// int count = 2;
		// for (int i = 0; i <=count; i++) {
		// if (end < 37680) {
		// start = i * 12560;
		// end = start + 12560;
		// } else {
		// start = i * 12560;
		// // 总行数了
		// end = 37680;
		// }
		// System.out.println("start :" + start+" end :" + end+" i :"+i);
		// threadPool.execute(createTask(i, start, end));
		// }
		// threadPool.waitFinish(); // 等待所有任务执行完毕
		// threadPool.closePool(); // 关闭线程池
		// long endTime = System.currentTimeMillis();
		// System.out.println("程序运行时间： " + (endTime - startTime) / 1000 + "s");
	}

	/**
	 * 完成对数据库的表的添加删除和修改的操作
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public boolean executeUpdate(String sql, List<Object> params) throws SQLException {
		boolean flag = false;
		int result = -1; // 表示当用户执行添加删除和修改的时候所影响数据库的行数
		pstmt = con.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			int index = 1;
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, i);
			}
		}

		result = pstmt.executeUpdate();
		flag = result > 0 ? true : false;

		return flag;
	}

	public boolean executeUpdate(String sql) throws SQLException {

		boolean flag = false;

		int result = -1; // 表示当用户执行添加删除和修改的时候所影响数据库的行数

		pstmt = con.prepareStatement(sql);
		result = pstmt.executeUpdate();
		pstmt.close();
		flag = result > 0 ? true : false;

		return flag;
	}

	/**
	 * 从数据库中查询数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> executeQuery(String sql, List<Object> params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int index = 1;
		pstmt = con.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while (resultSet.next()) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < cols_len; i++) {
				String cols_name = metaData.getColumnName(i + 1).toLowerCase();
				Object cols_value = resultSet.getObject(cols_name);
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}
		return list;

	}

	/**
	 * jdbc的封装可以用反射机制来封装,把从数据库中获取的数据封装到一个类的对象里
	 * 
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> executeQueryByRef(String sql, List<Object> params, Class<T> cls) throws Exception {
		List<T> list = new ArrayList<T>();
		int index = 1;
		pstmt = con.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while (resultSet.next()) {
			T resultObject = cls.newInstance(); // 通过反射机制创建实例
			for (int i = 0; i < cols_len; i++) {
				String cols_name = metaData.getColumnName(i + 1);
				Object cols_value = resultSet.getObject(cols_name);
				if (cols_value == null) {
					cols_value = "";
				}
				Field field = cls.getDeclaredField(cols_name);
				field.setAccessible(true); // 打开javabean的访问private权限
				field.set(resultObject, cols_value);
			}
			list.add(resultObject);
		}
		return list;

	}

	public void closeDB() {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从数据库中查询数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public LinkedHashMap<String, String> executeQuery(String sql) throws SQLException {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		pstmt = con.prepareStatement(sql);

		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while (resultSet.next()) {
			for (int i = 1; i < cols_len; i++) {
				String cols_name = metaData.getColumnName(i);
				String cols_value = resultSet.getString(i);
				map.put(cols_name, cols_value);
			}
		}
		pstmt.close();
		resultSet.close();
		return map;

	}

	/**
	 * 从数据库中查询数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String, String> executeQueryNormal(String sql) throws SQLException {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		pstmt = con.prepareStatement(sql);
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		String cols_name = "";
		while (resultSet.next()) {
			for (int i = 1; i <= cols_len; i++) {
				cols_name = metaData.getColumnName(i).toLowerCase();
				String cols_value = resultSet.getString(i);
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;

	}
	public int executeQueryThread(String sql) throws SQLException {
		pstmt = con.prepareStatement(sql);
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		int cols_value =0;
		while (resultSet.next()) {
			for (int i = 1; i <= cols_len; i++) {
				cols_value = resultSet.getInt(i);
			}
		}
		return cols_value;
		
	}

	public Map<String, String> executeQueryRedo(String sql) throws SQLException {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		pstmt = con.prepareStatement(sql);
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		String cols_name = "";
		int num = 0;
		while (resultSet.next()) {
			num++;
			for (int i = 1; i <= cols_len; i++) {
				cols_name = metaData.getColumnName(i).toLowerCase() + num;
				String cols_value = resultSet.getString(i);
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;

	}

	/**
	 * select time,wjm from ( select to_char(next_time,'yyyy/MM/dd hh24:mi:ss')
	 * as time, name as wjm from v$archived_log a where a.name is not null order
	 * by next_time asc ) where time > '2019/03/26 20:00:00'
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public Map<String, String> executeQueryRedoGD(String sql) throws SQLException {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		pstmt = con.prepareStatement(sql);
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while (resultSet.next()) {
			String time = "";
			String wjm = "";
			for (int i = 1; i <= cols_len; i++) {
				if (i == 1) {
					time = resultSet.getString(i);
				} else {
					wjm = resultSet.getString(i);
				}
			}
			map.put(time, wjm);
		}
		return map;

	}

	/**
	 * gettAndsett
	 * 
	 * @return
	 */

	public String getUSERNAME() {
		return USERNAME;
	}

	public int getLastcount() {
		return lastcount;
	}

	public void setLastcount(int lastcount) {
		this.lastcount = lastcount;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPre() {
		return pre;
	}

	public void setPre(int pre) {
		this.pre = pre;
	}

	public int getSux() {
		return sux;
	}

	public void setSux(int sux) {
		this.sux = sux;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		this.PASSWORD = pASSWORD;
	}

	public String getDRIVER() {
		return DRIVER;
	}

	public void setDRIVER(String dRIVER) {
		this.DRIVER = dRIVER;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSID() {
		return SID;
	}

	public void setSID(String sID) {
		this.SID = sID;
	}

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

	public String getTb_name() {
		return tb_name;
	}

	public void setTb_name(String tb_name) {
		this.tb_name = tb_name;
	}

	public String getTimeFied() {
		return timeFied;
	}

	public void setTimeFied(String timeFied) {
		this.timeFied = timeFied;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSjcz() {
		return sjcz;
	}

	public void setSjcz(String sjcz) {
		this.sjcz = sjcz;
	}

}
