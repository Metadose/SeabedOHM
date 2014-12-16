package com.seabed.ohm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public interface ISystemObject {

	public int getId();

	public void setId(int id);

	public JSONObject getDetails();

	public void setDetails(JSONObject objDetails);

	public void create();

	public void update();

	public long delete();

	public Set<String> getAllKeys(Class<?> clazz);

	public Map<String, List<String>> get(Class<?> clazz, int Id);

}
