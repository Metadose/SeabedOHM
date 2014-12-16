package com.seabed.ohm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.seabed.annotations.DBField;
import com.seabed.annotations.DBObject;
import com.seabed.annotations.DataType;
import com.seabed.util.TraceUtilities;
import com.seabed.util.Utilities;

public class RedisDAO {

	public static final String SEPARATOR = ":";
	public Jedis jedis;

	/**
	 * Get values. hmget(String key)<br>
	 * Retrieve the values associated to the specified fields.<br>
	 * Pass fields defined in the object.<br>
	 * 
	 * @param namespace
	 * @param id
	 * @param fields
	 * @return
	 */
	public Map<String, List<String>> get(Class<?> clazz, String id) {
		String namespace = clazz.getAnnotation(DBObject.class).namespace();
		if (namespace == null) {
			TraceUtilities.print("Namespace not present: " + clazz.toString());
			return null;
		}

		// Get the key.
		startConnection();
		String key = namespace + SEPARATOR + id;

		// Get the fields in this object.
		Map<String, List<String>> valueList = new HashMap<String, List<String>>();
		for (Field field : clazz.getFields()) {
			DBField fieldAnno = field.getAnnotation(DBField.class);
			if (fieldAnno == null) {
				continue;
			}
			String fieldName = field.getName();
			List<String> resultList = jedis.hmget(key, fieldName);
			valueList.put(fieldName, resultList);

		}
		TraceUtilities.print("hmget: " + key + " " + valueList.toString());

		closeConnection();
		return valueList;
	}

	/**
	 * Get all keys in a namespace.
	 */
	public Set<String> getAllKeys(Class<?> clazz) {
		String namespace = clazz.getAnnotation(DBObject.class).namespace();
		if (namespace == null) {
			TraceUtilities.print("Namespace not present: " + clazz.toString());
			return null;
		}
		startConnection();
		Set<String> keys = jedis.keys(namespace + ":*");
		closeConnection();
		return keys;
	}

	public void create(String id, Object obj) {
		hmset(true, id, obj);
	}

	public void update(String id, Object obj) {
		hmset(false, id, obj);
	}

	public long delete(Object obj, String id) {
		startConnection();
		String key = obj.getClass().getAnnotation(DBObject.class).namespace()
				+ SEPARATOR + id;
		long removed = jedis.del(key);
		TraceUtilities.print("del: " + key + " " + removed);
		closeConnection();
		return removed;
	}

	/**
	 * Create/Update a new value.
	 * 
	 * @param namespace
	 * @param obj
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void hmset(boolean isCreate, String id, Object obj) {
		// Get and check the namespace.
		String namespace = getNamespace(obj);
		if (namespace == null || id.isEmpty()) {
			TraceUtilities.print("Namespace or ID is not present: "
					+ obj.getClass());
			return;
		}

		startConnection();
		Class<?> clazz = obj.getClass();
		String key = namespace + SEPARATOR + id;
		Map<String, String> objData = new HashMap<String, String>();
		try {
			// Populate the map.
			for (Field field : clazz.getFields()) {

				// Get the annotation.
				DBField fieldAnno = field.getAnnotation(DBField.class);

				// If annotation does not exist, skip.
				if (fieldAnno == null) {
					continue;
				}
				// If exists and is a db field, put.

				Object value = field.get(obj);

				// If value is null.
				if (value == null) {
					objData.put(field.getName(), "");

				} else {

					int dataType = DataType.getDataType(field);
					// If value is a list.
					if (dataType == DataType.DATA_TYPE_LIST) {
						List<String> valueList = (ArrayList) value;
						String valStr = "";
						for (String val : valueList) {
							valStr += val + ",";
						}
						objData.put(field.getName(),
								Utilities.trimLastComma(valStr));
					}
					// If a JSON.
					else if (dataType == DataType.DATA_TYPE_JSON) {
						JSONObject jObject = (JSONObject) value;
						objData.put(field.getName(), jObject.toString());
					}
					// Everything else .
					else {
						objData.put(field.getName(),
								Utilities.convertToString(value));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Commit.
		jedis.hmset(key, objData);
		TraceUtilities.print("hmset: " + (isCreate ? "create" : "update") + " "
				+ key + " " + objData.toString());
		closeConnection();
	}

	/**
	 * Get a namespace given an object.
	 * 
	 * @param obj
	 * @return
	 */
	private String getNamespace(Object obj) {
		// Construct key and initial objs.
		Class<?> clazz = obj.getClass();
		DBObject classAnno = clazz.getAnnotation(DBObject.class);
		if (classAnno == null) {
			TraceUtilities.print("DBClassAnnotation not present in "
					+ obj.getClass());
			return null;
		}
		return classAnno.namespace();
	}

	/**
	 * Start a connection to the Redis server.
	 */
	public void startConnection() {
		if (jedis == null) {
			jedis = new Jedis("localhost");
		}
	}

	/**
	 * Close the connection.
	 */
	public void closeConnection() {
		if (jedis.isConnected()) {
			jedis.quit();
		}
	}

	/**
	 * Get the next increment of a namespace.
	 * 
	 * @param namespace
	 * @return
	 */
	public long getIncrement(String namespace) {
		return jedis.incr(namespace);
	}

	public void flushAll() {
		startConnection();
		jedis.flushAll();
		closeConnection();
	}

}
