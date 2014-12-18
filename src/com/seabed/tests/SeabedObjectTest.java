package com.seabed.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.seabed.ohm.SeabedEngine;
import com.seabed.ohm.exceptions.SeabedOHMException;

public class SeabedObjectTest {

	@Test
	public void testNamespace() throws SeabedOHMException {
		TestObjectWithIntID obj = new TestObjectWithIntID();
		assertEquals("test", obj.getNamespace());
	}

	@Test
	public void testPersistentFields() throws SeabedOHMException {
		TestObjectWithIntID obj = new TestObjectWithIntID();
		List<Field> fields = obj.getPersistentFields();

		assertTrue(!fields.isEmpty());
		assertEquals(2, fields.size());

		String f1 = fields.get(0).getName();
		String f2 = fields.get(1).getName();

		assertEquals("firstName", f1);
		assertEquals("lastName", f2);
	}

	@Test
	public void testIDField() throws SeabedOHMException {
		TestObjectWithIntID obj = new TestObjectWithIntID();
		assertEquals("1", obj.getId(true));

		TestObjectWithStringID obj2 = new TestObjectWithStringID();
		obj2.setId("testID");
		assertEquals("testID", obj2.getId(true));
	}

	// TODO
	@Test
	public void testAutoIncrement() throws SeabedOHMException {
		SeabedEngine eng = new SeabedEngine();
		TestObjectWithIntID obj = new TestObjectWithIntID();
		String ns = obj.getNamespace();

		assertEquals(1, eng.getIncrement(ns));
		obj.create();
		assertEquals(3, eng.getIncrement(ns));
	}

	@BeforeClass
	public static void before() {
		SeabedEngine eng = new SeabedEngine();
		eng.flushAll();
	}

	@AfterClass
	public static void after() {
		SeabedEngine eng = new SeabedEngine();
		eng.flushAll();
	}

}
