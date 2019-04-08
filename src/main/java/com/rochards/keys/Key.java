package com.rochards.keys;

import java.util.HashMap;
import java.util.Map;

public class Key {

	private int numberOfFields;
	private String name;
	private String field;
	private Map<String, String> fields;
	
	public Key(String name, String field, int numberOfFields) {
		
		this.name = name;
		this.field = field;
		this.numberOfFields = numberOfFields;
		this.fields = new HashMap<String, String>();
		this.setFields(this.field);
	}
	
	public String getName() {
		return this.name;
	}
	
	private void setFields(String field) {
		for (int i = 0; i < this.numberOfFields; i++) {
			this.fields.put("field"+i, field);
		}
	}
	
	public Map<String, String> getFields() {
		return this.fields;
	}
}
