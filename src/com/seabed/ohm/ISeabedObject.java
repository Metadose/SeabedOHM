package com.seabed.ohm;

public interface ISeabedObject {

	public void setId(Object idValue);

	public String getId(boolean isCreate);

	public void create();

	public void update();

	public long delete();

}
