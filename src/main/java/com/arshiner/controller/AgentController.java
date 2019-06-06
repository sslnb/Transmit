package com.arshiner.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.arshiner.common.ConfigManager;
import com.arshiner.common.JDBCUtil;
import com.arshiner.common.JsonToObject;

@Controller
@RequestMapping(value = "/")
public class AgentController {

	@Autowired
	ConfigManager config;

	/**
	 * @param map
	 * @return
	 */
	@RequestMapping("/properties")
	@ResponseBody
	public Object helloHtml(@RequestParam(value = "fip") String fip,@RequestParam(value = "orafip") String orafip,@RequestParam(value = "kip") String kip, @RequestParam(value = "port") String port,
			@RequestParam(value = "jlist") List<Object> jlist, @RequestParam(value = "mlist") List<Object> mlist) {
		System.out.println(fip);
		System.out.println(port);
		// 配置传输进来
		HashMap<String, Object> jm = new HashMap<>();
		int indexof = 0;
		String jgxtlb = "";
		String ml = "";
		for (int i = 0; i < jlist.size(); i++) {
			if (i == 0) {
				indexof = jlist.get(i).toString().lastIndexOf("=");
				jgxtlb = jlist.get(i).toString().substring(indexof + 1, jlist.get(i).toString().length());
				indexof = mlist.get(i).toString().lastIndexOf("=");
				ml = mlist.get(i).toString().substring(indexof + 1, mlist.get(i).toString().length());
				jm.put(jgxtlb, ml);
			} else {
				jm.put(jlist.get(i).toString(), mlist.get(i).toString());
			}
		}
		HashMap<String, Object> string = new HashMap<>();
		JSONObject Json = JsonToObject.MapconsvertToJson(jm);
		String peizhi = JsonToObject.JSONconsvertToString(Json);
		JDBCUtil jdbc = new JDBCUtil("adtmgr", "adtmgr", orafip, "1521", "orcl");
		boolean flag = true;
			flag = jdbc.getConnection();
			config.configGetAndSet("peizhi", peizhi);
			config.configGetAndSet("fip", fip);
			config.configGetAndSet("kip", kip);
			config.configGetAndSet("port", port);
			config.configGetAndSet("orafip", orafip);
			if (flag) {
				string.put("status", "配置成功");
				jdbc.closeDB();
			}else{
				string.put("status", "配置失败 ");
			}
		return JSONObject.toJSON(string);
	}

	/**
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/properties1", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE )
	public Object helloHtml1(@RequestParam String kip, @RequestParam String fip, @RequestParam String port,
			@RequestParam String filepath) {
		System.out.println("ip-----" + kip);
		System.out.println("port-----" + port);
		System.out.println("filepath" + filepath);
		File file = new File(filepath);
		if (!file.exists()) {
			HashMap<String, String> string = new HashMap<>();
			string.put("status", "文件夹不存在");
			return string;
		}
		HashMap<String, String> string = new HashMap<>();
		string.put("status", "配置成功");

		config.configGetAndSet("kip", kip);
		config.configGetAndSet("fip", fip);
		config.configGetAndSet("port", port);
		config.configGetAndSet("filepath", filepath);
		return string;
	}

	@RequestMapping("/")
	public String loginHtml() {

		return "login";
	}
}
