package com.seabed.sample;

import com.seabed.ohm.SeabedObject;
import com.seabed.ohm.annotations.AutoIncrement;
import com.seabed.ohm.annotations.ID;
import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.annotations.SBObject;
import com.seabed.ohm.exceptions.SeabedOHMException;

@SBObject(namespace = "sample")
public class SampleObject extends SeabedObject {

	public SampleObject() throws SeabedOHMException {
		super();
	}

	public SampleObject(Object id) throws SeabedOHMException {
		super(id);
	}

	@ID
	@AutoIncrement
	public int id;

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