package com.rochards.keys;

import java.util.HashMap;
import java.util.Map;

public class Key {

	private int numberOfFields;
	private String name;
	private String field;
	private Map<String, String> fields;
	
	/**
	   * Create a instance of Key
	   * @param name This is the identifier of key
	   * @param fields  This the field value
	   * @param numberOfFields This is the number of fields this rash contains
	   */
	public Key(String name, String field, int numberOfFields) {
		
		this.name = name;
		this.field = field;
		this.numberOfFields = numberOfFields;
		this.fields = new HashMap<String, String>();
		this.setFields(this.field);
	}
	
	/**
	 * @return name of this key
	 * */
	public String getName() {
		return this.name;
	}
	
	private void setFields(String field) {
		for (int i = 0; i < this.numberOfFields; i++) {
			this.fields.put("field"+i, field);
		}
	}
	
	/**
	 * @return all fields values
	 * */
	public Map<String, String> getFields() {
		return this.fields;
	}
}
