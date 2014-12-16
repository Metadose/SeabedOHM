package com.seabed.sample;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.seabed.annotations.DBClassAnnotation;
import com.seabed.annotations.DBFieldAnnotation;
import com.seabed.ohm.SystemObject;
import com.seabed.util.Utilities;

@DBClassAnnotation(namespace = "student")
public class Student extends SystemObject {

	@DBFieldAnnotation(isDBField = true, isList = true)
	public List<String> teachers;

	@DBFieldAnnotation(isDBField = true, isList = true)
	public List<String> subjects;

	@DBFieldAnnotation(isDBField = true)
	public String level;

	@DBFieldAnnotation(isDBField = true)
	public String section;

	@DBFieldAnnotation(isDBField = true)
	public String campus;

	@DBFieldAnnotation(isDBField = true)
	public String adviser;

	// Map of Day, to Time, to Subject and Room.
	@DBFieldAnnotation(isDBField = true, isJSON = true)
	public JSONObject schedule;

	public Student() {
		;
	}

	public Student(int Id) {
		setId(Id);
		init();
	}

	public Student(String studentID) {
		setId(Integer.valueOf(studentID));
	}

	public List<String> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<String> teachers) {
		this.teachers = teachers;
	}

	public void setTeachers(String... teachers) {
		this.teachers = Utilities.convertArrayToList(teachers);
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public void setSubjects(String... subjects) {
		this.subjects = Utilities.convertArrayToList(subjects);
	}

	public Map<String, List<String>> get(int Id) {
		return get(Student.class, Id);
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getCampus() {
		return campus;
	}

	public void setCampus(String campus) {
		this.campus = campus;
	}

	public String getAdviser() {
		return adviser;
	}

	public void setAdviser(String adviser) {
		this.adviser = adviser;
	}

	public JSONObject getSchedule() {
		return schedule;
	}

	public void setSchedule(JSONObject schedule) {
		this.schedule = schedule;
	}
}
