package com.seabed.sample;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seabed.ohm.SeabedObject;
import com.seabed.ohm.exceptions.SeabedOHMException;

public class Sample {

	public static void main(String[] args) throws SeabedOHMException {
		get();
	}

	public static void get() throws SeabedOHMException {
		Map<String, List<String>> object = SeabedObject.get(SampleObject.class,
				2);
		Set<String> keys = SeabedObject.getAllKeys(SampleObject.class);
	}

	public static void create() throws SeabedOHMException {
		SampleObject obj = new SampleObject(); // Construct the object.
		obj.setFirstName("John"); // Set obj first name.
		obj.setLastName("Doe"); // Set obj last name.
		obj.create(); // To add a new entry.
	}

	public static void delete() throws SeabedOHMException {
		SampleObject obj = new SampleObject(2); // Construct the object.
		obj.delete(); // Delete the entry.
	}

	public static void update() throws SeabedOHMException {
		SampleObject obj = new SampleObject(2); // Construct the object.
		obj.setFirstName("Jane"); // Change the name of John to Jane.
		obj.update(); // Update the entry.
	}

}
