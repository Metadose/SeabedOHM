package com.seabed.ohm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.seabed.ohm.annotations.DataType;
import com.seabed.ohm.annotations.ID;
import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.annotations.SBObject;
import com.seabed.ohm.exceptions.MultipleIDFieldFoundException;
import com.seabed.ohm.exceptions.NoIDFieldFoundException;
import com.seabed.ohm.exceptions.NoNamespaceFoundException;
import com.seabed.ohm.exceptions.NoPersistentFieldFoundException;
import com.seabed.ohm.exceptions.NoSBObjectAnnotationException;

public class SeabedObject implements ISeabedObject {

	public SeabedObject() throws NoNamespaceFoundException,
			NoPersistentFieldFoundException, NoIDFieldFoundException,
			MultipleIDFieldFoundException {
		if (!namespaceExists()) {
			throw new NoNamespaceFoundException();
		}
		if (getDBFields().isEmpty()) {
			throw new NoPersistentFieldFoundException();
		}
		int idCount = countIDFields();
		if (idCount == 0) {
			throw new NoIDFieldFoundException();
		} else if (idCount > 1) {
			throw new MultipleIDFieldFoundException();
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
			Persist anno = field.getAnnotation(Persist.class);
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
		SBObject anno = this.getClass().getAnnotation(SBObject.class);
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
				Persist anno = fieldObj.getAnnotation(Persist.class);
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
		try {
			dao.create(getId(), this);
		} catch (NoSBObjectAnnotationException e) {
			e.printStackTrace();
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		} catch (NoIDFieldFoundException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		RedisDAO dao = new RedisDAO();
		try {
			dao.update(getId(), this);
		} catch (NoSBObjectAnnotationException e) {
			e.printStackTrace();
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		} catch (NoIDFieldFoundException e) {
			e.printStackTrace();
		}
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
		try {
			return dao.getAllKeys(clazz);
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		}
		return new HashSet<String>();
	}

	public static Map<String, List<String>> get(Class<?> clazz, String Id) {
		RedisDAO dao = new RedisDAO();
		try {
			return dao.get(clazz, Id);
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		}
		return new HashMap<String, List<String>>();
	}
}
