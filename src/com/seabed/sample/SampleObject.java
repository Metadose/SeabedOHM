package com.seabed.sample;

import com.seabed.ohm.SeabedObject;
import com.seabed.ohm.annotations.AutoIncrement;
import com.seabed.ohm.annotations.ID;
import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.annotations.SBObject;
import com.seabed.ohm.exceptions.SeabedOHMException;

@SBObject(namespace = "test1")
public class SampleObject extends SeabedObject {

	public SampleObject() throws SeabedOHMException {
		super();
	}

	@ID
	@AutoIncrement
	public int id;

	@Persist
	public String test = "1";

	@Persist
	public String test2 = "2";

}