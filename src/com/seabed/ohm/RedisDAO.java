package com.seabed.ohm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.seabed.annotations.DBClassAnnotation;
import com.seabed.annotations.DBFieldAnnotation;
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
	public Map<String, List<String>> get(Class<?> clazz, long id) {
		String namespace = clazz.getAnnotation(DBClassAnnotation.class)
				.namespace();
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
			DBFieldAnnotation fieldAnno = field
					.getAnnotation(DBFieldAnnotation.class);
			if (fieldAnno == null) {
				continue;
			} else if (fieldAnno.isDBField()) {
				String fieldName = field.getName();
				List<String> resultList = jedis.hmget(key, fieldName);
				valueList.put(fieldName, resultList);
			}
		}
		TraceUtilities.print("hmget: " + key + " " + valueList.toString());

		closeConnection();
		return valueList;
	}

	/**
	 * Get all keys in a namespace.
	 */
	public Set<String> getAllKeys(Class<?> clazz) {
		String namespace = clazz.getAnnotation(DBClassAnnotation.class)
				.namespace();
		if (namespace == null) {
			TraceUtilities.print("Namespace not present: " + clazz.toString());
			return null;
		}
		startConnection();
		Set<String> keys = jedis.keys(namespace + ":*");
		closeConnection();
		return keys;
	}

	public void create(Object obj) {
		hmset(true, 0, obj);
	}

	public void update(long id, Object obj) {
		hmset(false, id, obj);
	}

	public long delete(Object obj, long id) {
		startConnection();
		String key = obj.getClass().getAnnotation(DBClassAnnotation.class)
				.namespace()
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
	public void hmset(boolean isCreate, long id, Object obj) {
		// Get and check the namespace.
		String namespace = getNamespace(obj);
		if (namespace == null) {
			TraceUtilities.print("Namespace not present: " + obj.getClass());
			return;
		}

		startConnection();
		Class<?> clazz = obj.getClass();
		id = isCreate ? getIncrement(namespace) : id;
		String key = namespace + SEPARATOR + id;
		Map<String, String> objData = new HashMap<String, String>();
		try {
			// Populate the map.
			for (Field field : clazz.getFields()) {

				// Get the annotation.
				DBFieldAnnotation fieldAnno = field
						.getAnnotation(DBFieldAnnotation.class);

				// If annotation does not exist, skip.
				if (fieldAnno == null) {
					continue;
				}
				// If exists and is a db field, put.
				else if (fieldAnno.isDBField()) {
					Object value = field.get(obj);

					// If value is null.
					if (value == null) {
						objData.put(field.getName(), "");
					}
					// If value is a list.
					else if (fieldAnno.isList()) {
						List<String> valueList = (ArrayList) value;
						String valStr = "";
						for (String val : valueList) {
							valStr += val + ",";
						}
						objData.put(field.getName(),
								Utilities.trimLastComma(valStr));
					}
					// If a JSON.
					else if (fieldAnno.isJSON()) {
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
		DBClassAnnotation classAnno = clazz
				.getAnnotation(DBClassAnnotation.class);
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
