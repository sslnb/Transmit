package com.arshiner.common;
/**
 * 类型转换
 * @author 士林
 *
 */
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
@Component
public class JsonToObject {
	/**
	 * Map转JSON
	 * @param hashmap
	 * @return
	 */
	public static JSONObject  MapconsvertToJson(Map<String,Object> map){
		
		JSONObject   json = new JSONObject(map);
		return json;
		
	}
	public static void main(String[] args) {
	}
	/**
	 * Json转String
	 * @param json
	 * @return
	 */
	public static String  JSONconsvertToString(JSONObject  json){
		String value = json.toJSONString();
		return value;
	}
	/**
	 * JSON转Map
	 * @param json
	 * @return
	 */
	public static Map<String ,Object> JSONconsvertToMap(JSONObject  json){
		 Map<String, Object> map = (Map<String, Object>)json;
		return map;
	}
	/**
	 * String转JSON
	 * String格式必须是此模式{"password":"123","username":"yaomy"}
	 * @param str
	 * @return
	 */
	public static JSONObject StringconsvertToJSONObject(String str){
		JSONObject json = JSONObject.parseObject(str);
		return json;
	}
	
}
