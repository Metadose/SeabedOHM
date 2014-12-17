package com.seabed.ohm;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.annotations.SBObject;
import com.seabed.ohm.exceptions.NoIDFieldFoundException;
import com.seabed.ohm.exceptions.NoNamespaceFoundException;
import com.seabed.ohm.exceptions.NoSBObjectAnnotationException;
import com.seabed.util.TraceUtilities;
import com.seabed.util.Utilities;

public class SeabedEngine {

	public static final String SEPARATOR = ":";
	public Jedis jedis;

	public Object getAsObj(Object obj, long id)
			throws NoNamespaceFoundException, IllegalAccessException,
			JSONException {
		return getAsObj(obj, String.valueOf(id));
	}

	/**
	 * Get values. hmget(String key)<br>
	 * Retrieve the values associated to the specified fields.<br>
	 * Pass fields defined in the object.<br>
	 * 
	 * @param namespace
	 * @param id
	 * @param fields
	 * @return
	 * @throws NoNamespaceFoundException
	 */
	public Object getAsObj(Object obj, String id)
			throws NoNamespaceFoundException, IllegalAccessException,
			JSONException {
		Class<?> clazz = obj.getClass();
		String namespace = clazz.getAnnotation(SBObject.class).namespace();
		if (namespace == null) {
			throw new NoNamespaceFoundException();
		}

		// Get the key.
		startConnection();
		String key = namespace + SEPARATOR + id;

		// Get the fields in this object.
		for (Field field : clazz.getFields()) {
			Persist fieldAnno = field.getAnnotation(Persist.class);
			if (fieldAnno == null) {
				continue;
			}
			String fieldName = field.getName();
			List<String> resultList = jedis.hmget(key, fieldName);
			String firstElem = resultList.get(0);

			// Set the value inside the object.
			if (firstElem != null) {
				try {
					Field classField = clazz.getField(fieldName);
					classField = getClassFieldValue(classField, resultList,
							obj, field);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}

		}

		closeConnection();
		return obj;
	}

	private Field getClassFieldValue(Field classField, List<String> resultList,
			Object obj, Field field) {
		String firstElem = resultList.get(0);
		int dataType = DataType.getDataType(obj, field);

		try {
			if (dataType == DataType.DATA_TYPE_BOOLEAN) {
				classField.setBoolean(obj, Boolean.valueOf(firstElem));

			} else if (dataType == DataType.DATA_TYPE_FLOAT) {
				classField.setFloat(obj, Float.valueOf(firstElem));

			} else if (dataType == DataType.DATA_TYPE_JSON) {
				classField.set(obj, new JSONObject(firstElem));

			} else if (dataType == DataType.DATA_TYPE_LIST) {
				classField.set(obj, resultList);

			} else if (dataType == DataType.DATA_TYPE_LONG) {
				classField.setLong(obj, Long.valueOf(firstElem));

			} else if (dataType == DataType.DATA_TYPE_INT) {
				classField.setInt(obj, Integer.valueOf(firstElem));

			} else if (dataType == DataType.DATA_TYPE_STRING) {
				classField.set(obj, firstElem);

			} else if (dataType == DataType.DATA_TYPE_TIMESTAMP) {
				classField.set(obj, Timestamp.valueOf(firstElem));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classField;
	}

	/**
	 * Get values. hmget(String key)<br>
	 * Retrieve the values associated to the specified fields.<br>
	 * Pass fields defined in the object.<br>
	 * 
	 * @param namespace
	 * @param id
	 * @param fields
	 * @return
	 * @throws NoNamespaceFoundException
	 */
	public Map<String, List<String>> getAsMap(Class<?> clazz, String id)
			throws NoNamespaceFoundException {
		String namespace = clazz.getAnnotation(SBObject.class).namespace();
		if (namespace == null) {
			throw new NoNamespaceFoundException();
		}

		// Get the key.
		startConnection();
		String key = namespace + SEPARATOR + id;

		// Get the fields in this object.
		Map<String, List<String>> valueList = new HashMap<String, List<String>>();
		for (Field field : clazz.getFields()) {
			Persist fieldAnno = field.getAnnotation(Persist.class);
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
	 * 
	 * @throws NoNamespaceFoundException
	 */
	public Set<String> getAllKeys(Class<?> clazz)
			throws NoNamespaceFoundException {
		String namespace = clazz.getAnnotation(SBObject.class).namespace();
		if (namespace == null) {
			throw new NoNamespaceFoundException();
		}
		startConnection();
		Set<String> keys = jedis.keys(namespace + ":*");
		closeConnection();
		return keys;
	}

	/**
	 * Create a new entry in the database
	 * 
	 * @param id
	 * @param obj
	 * @throws NoSBObjectAnnotationException
	 * @throws NoNamespaceFoundException
	 * @throws NoIDFieldFoundException
	 */
	public void create(String id, Object obj)
			throws NoSBObjectAnnotationException, NoNamespaceFoundException,
			NoIDFieldFoundException {
		hmset(true, id, obj);
	}

	/**
	 * Update an existing entry in the database.
	 * 
	 * @param id
	 * @param obj
	 * @throws NoSBObjectAnnotationException
	 * @throws NoNamespaceFoundException
	 * @throws NoIDFieldFoundException
	 */
	public void update(String id, Object obj)
			throws NoSBObjectAnnotationException, NoNamespaceFoundException,
			NoIDFieldFoundException {
		hmset(false, id, obj);
	}

	/**
	 * Delete an entry in the database.
	 * 
	 * @param obj
	 * @param id
	 * @return
	 */
	public long delete(Object obj, String id) {
		startConnection();
		String key = obj.getClass().getAnnotation(SBObject.class).namespace()
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
	 * @throws NoSBObjectAnnotationException
	 * @throws NoNamespaceFoundException
	 * @throws NoIDFieldFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void hmset(boolean isCreate, String id, Object obj)
			throws NoSBObjectAnnotationException, NoNamespaceFoundException,
			NoIDFieldFoundException {
		// Get and check the namespace.
		String namespace = getNamespace(obj);
		if (namespace == null) {
			throw new NoNamespaceFoundException();
		}
		if (id.isEmpty()) {
			throw new NoIDFieldFoundException();
		}

		startConnection();
		Class<?> clazz = obj.getClass();
		String key = namespace + SEPARATOR + id;
		Map<String, String> objData = new HashMap<String, String>();
		try {
			// Populate the map.
			for (Field field : clazz.getFields()) {

				// Get the annotation.
				Persist fieldAnno = field.getAnnotation(Persist.class);

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

					int dataType = DataType.getDataType(obj, field);
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
	 * @throws NoSBObjectAnnotationException
	 */
	private String getNamespace(Object obj)
			throws NoSBObjectAnnotationException {
		// Construct key and initial objs.
		Class<?> clazz = obj.getClass();
		SBObject classAnno = clazz.getAnnotation(SBObject.class);
		if (classAnno == null) {
			throw new NoSBObjectAnnotationException();
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
		startConnection();
		long inc = jedis.incr(namespace);
		closeConnection();
		return inc;
	}

	public void flushAll() {
		startConnection();
		jedis.flushAll();
		closeConnection();
	}

	public static void main(String[] args) {
		Jedis jedis2 = new Jedis("localhost");
		jedis2.flushAll();
		jedis2.quit();
	}

}
