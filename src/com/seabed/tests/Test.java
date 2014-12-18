package com.seabed.tests;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seabed.ohm.SeabedObject;
import com.seabed.ohm.exceptions.SeabedOHMException;

public class Test {

	public static void main(String[] args) throws SeabedOHMException {
		getObj();
	}

	// TODO Work on this feature.
	public static void getObj() throws SeabedOHMException {
		Object obj = SeabedObject.getAsObj(new TestObjectWithIntID(), 2);
		obj.toString();
	}

	public static void get() throws SeabedOHMException {
		Map<String, List<String>> objectMap = SeabedObject.getAsMap(
				TestObjectWithIntID.class, 2);
		Set<String> keys = SeabedObject.getAllKeys(TestObjectWithIntID.class);
	}

	public static void create() throws SeabedOHMException {
		TestObjectWithIntID obj = new TestObjectWithIntID(); // Construct the object.
		obj.setFirstName("John"); // Set obj first name.
		obj.setLastName("Doe"); // Set obj last name.
		obj.create(); // To add a new entry.
	}

	public static void delete() throws SeabedOHMException {
		TestObjectWithIntID obj = new TestObjectWithIntID(2); // Construct the object.
		obj.delete(); // Delete the entry.
	}

	public static void update() throws SeabedOHMException {
		TestObjectWithIntID obj = new TestObjectWithIntID(2); // Construct the object.
		obj.setFirstName("Jane"); // Change the name of John to Jane.
		obj.update(); // Update the entry.
	}

}
