package com.seabed.sample;

import com.seabed.annotations.ID;
import com.seabed.annotations.Persist;
import com.seabed.exceptions.SeabedOHMException;
import com.seabed.ohm.SeabedObject;

public class SampleObject extends SeabedObject {

	public SampleObject() throws SeabedOHMException {
		super();
	}

	@ID
	public String id = "idKo";

	@Persist
	public String test = "1";

	@Persist
	public String test2 = "2";

}