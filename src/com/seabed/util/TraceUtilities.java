package com.seabed.util;

import com.sun.jmx.snmp.Timestamp;

public class TraceUtilities {

	public static void print(String string) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		System.out.println(stamp.getDate() + " " + string);
	}

	public static void main(String[] args) {
		print("test");
	}

}
