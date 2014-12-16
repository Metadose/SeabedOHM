package com.seabed.sample;

import com.seabed.annotations.DBField;
import com.seabed.annotations.DBObject;
import com.seabed.annotations.ID;
import com.seabed.ohm.SeabedObject;

@DBObject(namespace = "sample")
public class SampleObject extends SeabedObject {

	@ID
	public String id = "idKo";

	@DBField
	public String test = "1";

	@DBField
	public String test2 = "2";

}