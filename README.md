# SeabedOHM

SeabedOHM is an `Object-Hash Mapping` Java persistence library inspired by HibernateORM implemented for `Redis`.


## Dependencies
1. `java-json.jar` Java JSON library
2. `jedis-2.0.0.jar` Java Redis Client


## Sample
```java
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

}
```


## Creating a Persistent Object

### Namespace
```java
@SBObject(namespace = "sample")
```
* Declare an `@SBObject` annotation on top of the Class.
* Set the value of the `namespace` as the namespace that would correspond in Redis.
* For example, in SQL, it's more like the `name of the table`.

### Class Extend
```java
public class SampleObject extends SeabedObject
```
* Extend your Object to `SeabedObject`
* This would enable you to use core functionalities of the object (i.e., CRUD).

### Constructor
```java
	public SampleObject() throws SeabedOHMException {
		super();
	}
	
	public SampleObject(Object id) throws SeabedOHMException {
		super(id);
	}
```
* Add a `constructor` which throws `SeabedOHMException`
* Don't worry, if you're using `Eclipse`, this would just auto-suggest.

### ID Field
```java
	@ID
	@AutoIncrement
	public int id;
```
* Declare an `ID field` that would become your `unique identifier` for every entry of this object.
* On top of your ID field, declare an `@ID annotation`.
* The ID field can be any `Number` (e.g., int, long, short, etc.) or a `String`.

### Persistent Fields
```java
	@Persist
	public String firstName;

	@Persist
	public String lastName;
```
* To flag a field as `Persistent`, add the annotation `@Persist` on top of the field.
* Currently `supported data types` are the following:
1. Number (e.g., int, long, short, double, etc.)
2. String
3. List<String>
4. JSONObject


## Usage

### Create
```java
public static void create() throws SeabedOHMException {
	SampleObject obj = new SampleObject(); // Construct the object.
	obj.setFirstName("John"); // Set obj first name.
	obj.setLastName("Doe"); // Set obj last name.
	obj.create(); // To add a new entry.
}
```

### Update
```java
public static void update() throws SeabedOHMException {
	SampleObject obj = new SampleObject(1); // Construct the object.
	obj.setFirstName("Jane"); // Change the name of John to Jane.
	obj.update(); // Update the entry.
}
```

### Delete
```java
public static void delete() throws SeabedOHMException {
	SampleObject obj = new SampleObject(1); // Construct the object.
	obj.delete(); // Delete the entry.
}
```
