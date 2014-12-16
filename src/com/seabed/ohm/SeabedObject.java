package com.seabed.ohm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.seabed.annotations.DBField;
import com.seabed.annotations.DBObject;
import com.seabed.annotations.DataType;
import com.seabed.annotations.ID;
import com.seabed.util.TraceUtilities;

public class SeabedObject implements ISeabedObject {

	public SeabedObject() {
		if (!namespaceExists()) {
			TraceUtilities.print("DBClassAnnotation does not exist in "
					+ this.getClass());
		}
		if (getDBFields().isEmpty()) {
			TraceUtilities.print("No DBFieldAnnotation fields defined in "
					+ this.getClass());
		}
		int idCount = countIDFields();
		if (idCount == 0) {
			TraceUtilities.print("No ID field defined in " + this.getClass());
		} else if (idCount > 1) {
			TraceUtilities.print("More than one ID field defined in "
					+ this.getClass());
		}
	}

	/**
	 * Count number of ID fields.
	 * 
	 * @return
	 */
	private int countIDFields() {
		Class<?> clazz = this.getClass();
		int i = 0;
		for (Field field : clazz.getFields()) {
			ID idField = field.getAnnotation(ID.class);
			if (idField == null) {
				continue;
			}
			i++;
		}
		return i;
	}

	/**
	 * Get list of all fields.
	 * 
	 * @return
	 */
	public List<String> getDBFields() {
		List<String> dbFields = new ArrayList<String>();
		for (Field field : this.getClass().getFields()) {
			DBField anno = field.getAnnotation(DBField.class);
			if (anno != null) {
				dbFields.add(field.getName());
			}
		}
		return dbFields;
	}

	/**
	 * Check if there is a defined namespace.
	 * 
	 * @return
	 */
	private boolean namespaceExists() {
		DBObject anno = this.getClass().getAnnotation(DBObject.class);
		return anno == null ? false : !anno.namespace().isEmpty();
	}

	/**
	 * Initialize the object.
	 */
	public void init() {
		Class<?> clazz = this.getClass();
		Map<String, List<String>> objectMap = get(clazz, getId());
		try {
			for (String field : objectMap.keySet()) {
				Field fieldObj = clazz.getField(field);

				List<String> value = objectMap.get(field);
				DBField anno = fieldObj.getAnnotation(DBField.class);
				if (anno == null) {
					continue;
				}

				int dataType = DataType.getDataType(fieldObj);
				if (dataType == DataType.DATA_TYPE_LIST) {
					fieldObj.set(this, value);
				} else {
					if (dataType == DataType.DATA_TYPE_JSON) {
						String jsonStr = value.get(0);
						fieldObj.set(this, new JSONObject(jsonStr));
					} else {
						fieldObj.set(this, value.get(0));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getId() {
		// Get the class.
		Class<?> clazz = this.getClass();
		String value = "";

		// Loop through each field in the class.
		for (Field field : clazz.getFields()) {

			// If field is not ID, skip it.
			ID idField = field.getAnnotation(ID.class);
			if (idField == null) {
				continue;
			}
			try {
				Object val = field.get(this);
				value = val != null ? String.valueOf(val) : "";
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			break;
		}
		return value;
	}

	public void create() {
		RedisDAO dao = new RedisDAO();
		dao.create(getId(), this);
	}

	public void update() {
		RedisDAO dao = new RedisDAO();
		dao.update(getId(), this);
	}

	public long delete() {
		RedisDAO dao = new RedisDAO();
		return dao.delete(this, getId());
	}

	/**
	 * Get all keys listed in Redis.
	 */
	public static Set<String> getAllKeys(Class<?> clazz) {
		RedisDAO dao = new RedisDAO();
		return dao.getAllKeys(clazz);
	}

	public static Map<String, List<String>> get(Class<?> clazz, String Id) {
		RedisDAO dao = new RedisDAO();
		return dao.get(clazz, Id);
	}
}
