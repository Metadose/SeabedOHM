package com.seabed.sample;

import com.seabed.ohm.SeabedObject;
import com.seabed.ohm.annotations.ID;
import com.seabed.ohm.annotations.Persist;
import com.seabed.ohm.exceptions.SeabedOHMException;

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