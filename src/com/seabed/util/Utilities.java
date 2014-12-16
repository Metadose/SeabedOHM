package com.seabed.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Helper functions to verify SMS-related values and parameters.
 * 
 * @author Victorio Cebedo II
 * 
 */
public class Utilities {

	/**
	 * Convert date string to timestamp.
	 * 
	 * @param dateString
	 * @return
	 */
	public static Timestamp parseTimestamp(String dateString) {
		Timestamp timestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(dateString);
			timestamp = new Timestamp(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * Check if valid number.
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isNumber(String number) {
		try {
			Long.parseLong(number);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Check if all characters are uppercase.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAllUpperCase(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// If char is not a letter, skip it.
			if (!Character.isLetter(c)) {
				continue;
			}
			if (c >= 97 && c <= 122) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert array to list of objects.
	 * 
	 * @param array
	 * @return
	 */
	public static ArrayList<String> convertArrayToList(String[] array) {
		ArrayList<String> elemList = new ArrayList<String>();
		for (String element : array) {
			elemList.add(element);
		}
		return elemList;
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String str) {
		return parseInt(str, 0);
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			;
		}
		return defaultValue;
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static float parseFloat(String str, float defaultValue) {
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			;
		}
		return defaultValue;
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static float parseFloat(String str) {
		return parseFloat(str, 0);
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static long parseLong(String str) {
		return parseLong(str, 0);
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static boolean parseBoolean(String str) {
		return parseBoolean(str, false);
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static long parseLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			;
		}
		return defaultValue;
	}

	/**
	 * Parse and handle irrelevant inputs.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static boolean parseBoolean(String str, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			;
		}
		return defaultValue;
	}

	public static String capitalizeFirst(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Get contents of a file as string.
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileContents(File file) {
		String content = "";
		try {
			Scanner scanner = new Scanner(file);
			content = scanner.useDelimiter("\\Z").next();
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * Get the query from the prepared statement.
	 * 
	 * @param preparedStatement
	 * @return
	 */
	public String getSqlQuery(PreparedStatement preparedStatement) {
		String stmtAsString = preparedStatement.toString();
		stmtAsString = stmtAsString.replace(
				"com.mysql.jdbc.JDBC4PreparedStatement", "");
		int colonIndex = stmtAsString.indexOf(":");
		String strToRemove = stmtAsString.substring(0, colonIndex + 2);
		stmtAsString = stmtAsString.replace(strToRemove, "");
		return stmtAsString;
	}

	/**
	 * Convert any object to String.
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertToString(Object obj) {
		return String.valueOf(obj);
	}

	public static String trimLastComma(String str) {
		return str == null || str.isEmpty() ? "" : str.substring(0,
				str.length() - 1);
	}
}
