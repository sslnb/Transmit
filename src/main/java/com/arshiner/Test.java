package com.arshiner;


import java.util.HashMap;
import java.util.Map;

import com.arshiner.common.JsonToObject;

public class Test {
	public static void main(String[] args) {
		
		Map<String, Object> str1 = new HashMap<>();
		str1.put("thread1", "725378");
		str1.put("thread2", "538054");
		System.out.println(JsonToObject.MapconsvertToJson(str1));
		
		
		
	}
}
