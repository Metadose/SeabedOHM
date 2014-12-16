package com.seabed.sample;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


public class Sample {
	public static void main(String[] args) {
		test2();
	}

	public static void test3() {
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("subject", "eng");
		map2.put("room", "rm1");

		Map<String, Map<String, String>> map1 = new HashMap<String, Map<String, String>>();
		map1.put("time1", map2);

		JSONObject obj = new JSONObject(map1);
		Student s = new Student();
		s.setSchedule(obj);
		s.create();
	}

	public static void test2() {
		Student s = new Student(5);
		s.getId();
	}

	public static void test1() {
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("subject", "eng");
		map2.put("room", "rm1");
		JSONObject map2JSON = new JSONObject(map2);

		Map<String, JSONObject> map1 = new HashMap<String, JSONObject>();
		map1.put("time1", map2JSON);
		JSONObject map1JSON = new JSONObject(map1);

		Map<String, JSONObject> map0 = new HashMap<String, JSONObject>();
		map0.put("mon", map1JSON);

		JSONObject obj = new JSONObject(map0);
		Student s = new Student();
		s.setSchedule(obj);
		s.create();
	}
}
