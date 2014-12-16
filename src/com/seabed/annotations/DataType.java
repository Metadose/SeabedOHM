package com.seabed.annotations;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

import org.json.JSONObject;

public abstract class DataType {

	public static final int DATA_TYPE_NUMBER = 1;
	public static final int DATA_TYPE_FLOAT = 2;
	public static final int DATA_TYPE_BOOLEAN = 3;
	public static final int DATA_TYPE_STRING = 4;
	public static final int DATA_TYPE_TIMESTAMP = 5;
	public static final int DATA_TYPE_LIST = 6;
	public static final int DATA_TYPE_JSON = 7;

	public static int getDataType(Field field) {
		int dataType = 0;
		Class<?> dataTypeClazz = field.getClass();

		if (dataTypeClazz == List.class) {
			dataType = DataType.DATA_TYPE_LIST;

		} else if (dataTypeClazz == JSONObject.class) {
			dataType = DataType.DATA_TYPE_JSON;

		} else if (dataTypeClazz == Timestamp.class) {
			dataType = DataType.DATA_TYPE_TIMESTAMP;

		} else if (dataTypeClazz == String.class) {
			dataType = DataType.DATA_TYPE_STRING;

		} else if (dataTypeClazz == Boolean.class
				|| dataTypeClazz == Boolean.TYPE) {
			dataType = DataType.DATA_TYPE_BOOLEAN;

		} else if (dataTypeClazz == Float.class || dataTypeClazz == Float.TYPE) {
			dataType = DataType.DATA_TYPE_FLOAT;

		} else if (dataTypeClazz == Integer.class
				|| dataTypeClazz == Integer.TYPE || dataTypeClazz == Long.class
				|| dataTypeClazz == Long.TYPE || dataTypeClazz == Double.class
				|| dataTypeClazz == Double.TYPE || dataTypeClazz == Short.class
				|| dataTypeClazz == Short.TYPE) {
			dataType = DataType.DATA_TYPE_NUMBER;

		}
		return dataType;
	}

}