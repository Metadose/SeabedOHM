package com.seabed.ohm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.seabed.ohm.annotations.AutoIncrement;
import com.seabed.ohm.annotations.ID;
import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.annotations.SBObject;
import com.seabed.ohm.exceptions.AutoIncrementNotNumberException;
import com.seabed.ohm.exceptions.MultipleIDFieldFoundException;
import com.seabed.ohm.exceptions.NoIDFieldFoundException;
import com.seabed.ohm.exceptions.NoNamespaceFoundException;
import com.seabed.ohm.exceptions.NoPersistentFieldFoundException;
import com.seabed.ohm.exceptions.NoSBObjectAnnotationException;

public class SeabedObject implements ISeabedObject {

	private List<Field> allFields;
	private Field idField;
	private boolean isIDAutoIncrement;
	private int idDataType;
	private String namespace;

	/**
	 * Construct a fresh object.
	 * 
	 * @throws NoNamespaceFoundException
	 * @throws NoPersistentFieldFoundException
	 * @throws NoIDFieldFoundException
	 * @throws MultipleIDFieldFoundException
	 * @throws AutoIncrementNotNumberException
	 */
	public SeabedObject() throws NoNamespaceFoundException,
			NoPersistentFieldFoundException, NoIDFieldFoundException,
			MultipleIDFieldFoundException, AutoIncrementNotNumberException {
		check();
	}

	/**
	 * On constructing an object based on a stored entry.
	 * 
	 * @param id
	 * @throws NoNamespaceFoundException
	 * @throws NoPersistentFieldFoundException
	 * @throws NoIDFieldFoundException
	 * @throws MultipleIDFieldFoundException
	 * @throws AutoIncrementNotNumberException
	 */
	public SeabedObject(Object id) throws NoNamespaceFoundException,
			NoPersistentFieldFoundException, NoIDFieldFoundException,
			MultipleIDFieldFoundException, AutoIncrementNotNumberException {
		check();
		init(String.valueOf(id));
		setId(id);
	}

	private void check() throws NoNamespaceFoundException,
			NoPersistentFieldFoundException, NoIDFieldFoundException,
			MultipleIDFieldFoundException, AutoIncrementNotNumberException {
		checkNamespace();
		checkPersistentFields();
		checkIDField();
		checkAutoIncrement();
	}

	/**
	 * Check the auto increment.
	 * 
	 * @return
	 * @throws AutoIncrementNotPersistentException
	 * @throws AutoIncrementNotNumberException
	 */
	private void checkAutoIncrement() throws AutoIncrementNotNumberException {

		// Loop through all fields.
		for (Field field : this.getClass().getFields()) {
			AutoIncrement autoInc = field.getAnnotation(AutoIncrement.class);

			// If not present, skip it.
			if (autoInc == null) {
				continue;
			}

			// If exists but not a number.
			if (getIdDataType() != DataType.DATA_TYPE_NUMBER) {
				throw new AutoIncrementNotNumberException();
			}
			setIDAutoIncrement(true);
			return;
		}
		setIDAutoIncrement(false);
	}

	/**
	 * Check validity of the ID field.
	 * 
	 * @throws NoIDFieldFoundException
	 * @throws MultipleIDFieldFoundException
	 */
	private void checkIDField() throws NoIDFieldFoundException,
			MultipleIDFieldFoundException {
		int idCount = countIDFields();
		if (idCount == 0) {
			throw new NoIDFieldFoundException();
		} else if (idCount > 1) {
			throw new MultipleIDFieldFoundException();
		}
		Field field = getIDAnnotatedField();
		int dataType = DataType.getDataType(this, field);
		setIdDataType(dataType);
		setIdField(field);
	}

	/**
	 * Get the field with ID annotation.
	 * 
	 * @return
	 */
	private Field getIDAnnotatedField() {
		Class<?> clazz = this.getClass();
		for (Field field : clazz.getFields()) {
			ID idField = field.getAnnotation(ID.class);
			if (idField == null) {
				continue;
			}
			return field;
		}
		return null;
	}

	/**
	 * Initialize this class' persistent fields.
	 * 
	 * @throws NoPersistentFieldFoundException
	 */
	private void checkPersistentFields() throws NoPersistentFieldFoundException {
		if (!persistentFieldsExists()) {
			throw new NoPersistentFieldFoundException();
		}
		setAllFields(getPersistentFields());
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
	public List<Field> getPersistentFields() {
		List<Field> dbFields = new ArrayList<Field>();
		for (Field field : this.getClass().getFields()) {
			Persist anno = field.getAnnotation(Persist.class);
			if (anno != null) {
				dbFields.add(field);
			}
		}
		return dbFields;
	}

	/**
	 * Get list of all fields.
	 * 
	 * @return
	 */
	public boolean persistentFieldsExists() {
		for (Field field : this.getClass().getFields()) {
			Persist anno = field.getAnnotation(Persist.class);
			if (anno != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initialize the namespace.
	 * 
	 * @return
	 * @throws NoNamespaceFoundException
	 */
	private boolean checkNamespace() throws NoNamespaceFoundException {
		if (!namespaceExists()) {
			throw new NoNamespaceFoundException();
		}
		SBObject anno = this.getClass().getAnnotation(SBObject.class);
		setNamespace(anno.namespace());
		return true;
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
	public void init(String id) {
		Class<?> clazz = this.getClass();
		Map<String, List<String>> objectMap = get(clazz, id);
		try {
			for (String field : objectMap.keySet()) {
				Field fieldObj = clazz.getField(field);

				List<String> value = objectMap.get(field);
				Persist anno = fieldObj.getAnnotation(Persist.class);
				if (anno == null) {
					continue;
				}

				if (getIdDataType() == DataType.DATA_TYPE_LIST) {
					fieldObj.set(this, value);
				} else {
					if (getIdDataType() == DataType.DATA_TYPE_JSON) {
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

	/**
	 * Set the value of the ID field.
	 * 
	 * @param idValue
	 */
	public void setId(Object idValue) {
		Field idField = getIdField();
		try {
			idField.set(this, idValue);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public String getId(boolean isCreate) {
		String value = "";

		// If ID is a number and is set to auto-increment.
		if (getIdDataType() == DataType.DATA_TYPE_NUMBER && isIDAutoIncrement()
				&& isCreate) {
			RedisDAO dao = new RedisDAO();
			long nextIncr = dao.getIncrement(getNamespace());
			value = String.valueOf(nextIncr);
		}
		// If not a number, like String.
		else {
			try {
				Field idField = getIdField();
				Object val = idField.get(this);
				value = val != null ? String.valueOf(val) : "";
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * Create a new entry in the Redis db.
	 */
	public void create() {
		RedisDAO dao = new RedisDAO();
		try {
			dao.create(getId(true), this);
		} catch (NoSBObjectAnnotationException e) {
			e.printStackTrace();
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		} catch (NoIDFieldFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the persistent fields of this class.<br>
	 * The new values in Redis will be whatever is the current value of the
	 * persistent variables.
	 */
	public void update() {
		RedisDAO dao = new RedisDAO();
		try {
			dao.update(getId(false), this);
		} catch (NoSBObjectAnnotationException e) {
			e.printStackTrace();
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		} catch (NoIDFieldFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete this entry.
	 */
	public long delete() {
		RedisDAO dao = new RedisDAO();
		return dao.delete(this, getId(false));
	}

	/**
	 * Get all keys listed in Redis associated with this class.
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

	public static Map<String, List<String>> get(Class<?> clazz, long Id) {
		return get(clazz, String.valueOf(Id));
	}

	/**
	 * Get the object map given a class name and an ID.
	 * 
	 * @param clazz
	 * @param Id
	 * @return
	 */
	public static Map<String, List<String>> get(Class<?> clazz, String Id) {
		RedisDAO dao = new RedisDAO();
		try {
			return dao.get(clazz, Id);
		} catch (NoNamespaceFoundException e) {
			e.printStackTrace();
		}
		return new HashMap<String, List<String>>();
	}

	@SuppressWarnings("unused")
	private List<Field> getAllFields() {
		return allFields;
	}

	private void setAllFields(List<Field> allFields) {
		this.allFields = allFields;
	}

	private Field getIdField() {
		return idField;
	}

	private void setIdField(Field idField) {
		this.idField = idField;
	}

	private String getNamespace() {
		return namespace;
	}

	private void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	private int getIdDataType() {
		return idDataType;
	}

	private void setIdDataType(int idDataType) {
		this.idDataType = idDataType;
	}

	private boolean isIDAutoIncrement() {
		return isIDAutoIncrement;
	}

	private void setIDAutoIncrement(boolean isIDAutoIncrement) {
		this.isIDAutoIncrement = isIDAutoIncrement;
	}

}
