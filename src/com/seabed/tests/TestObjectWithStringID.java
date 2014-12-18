package com.seabed.tests;

import com.seabed.ohm.SeabedObject;
import com.seabed.ohm.annotations.ID;
import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.annotations.SBObject;
import com.seabed.ohm.exceptions.SeabedOHMException;

@SBObject(namespace = "test2")
public class TestObjectWithStringID extends SeabedObject {

	public TestObjectWithStringID() throws SeabedOHMException {
		super();
	}

	public TestObjectWithStringID(Object id) throws SeabedOHMException {
		super(id);
	}

	@ID
	public String id = "testID";

	@Persist
	public String firstName;

	@Persist
	public String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}