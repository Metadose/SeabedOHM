package com.seabed.ohm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.seabed.annotations.DBFieldAnnotation;

public abstract class SystemObject implements ISystemObject {
	public int id;

	// Map of details.
	// String to String, where String is Detail ID.
	// String is value.
	@DBFieldAnnotation(isDBField = true, isJSON = true)
	public JSONObject details;

	/**
	 * Get list of all fields.
	 * 
	 * @return
	 */
	public List<String> getDBFields() {
		List<String> dbFields = new ArrayList<String>();
		for (Field field : this.getClass().getFields()) {
			DBFieldAnnotation anno = field
					.getAnnotation(DBFieldAnnotation.class);
			if (anno != null && anno.isDBField()) {
				dbFields.add(field.getName());
			}
		}
		return dbFields;
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
				DBFieldAnnotation anno = fieldObj
						.getAnnotation(DBFieldAnnotation.class);

				if (anno.isList()) {
					if (value.isEmpty()) {
						continue;
					}
					fieldObj.set(this, value);
				} else {
					if (value.get(0).isEmpty()) {
						continue;
					}
					if (anno.isJSON()) {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JSONObject getDetails() {
		return details;
	}

	public void setDetails(JSONObject objDetails) {
		this.details = objDetails;
	}

	public void create() {
		RedisDAO dao = new RedisDAO();
		dao.create(this);
	}

	public void update() {
		RedisDAO dao = new RedisDAO();
		dao.update(getId(), this);
	}

	public long delete() {
		RedisDAO dao = new RedisDAO();
		return dao.delete(this, getId());
	}

	public Set<String> getAllKeys(Class<?> clazz) {
		RedisDAO dao = new RedisDAO();
		return dao.getAllKeys(clazz);
	}

	public Map<String, List<String>> get(Class<?> clazz, int Id) {
		RedisDAO dao = new RedisDAO();
		return dao.get(clazz, Id);
	}
}
