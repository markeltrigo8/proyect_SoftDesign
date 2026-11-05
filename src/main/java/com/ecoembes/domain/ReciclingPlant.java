package com.ecoembes.domain;

public class ReciclingPlant {
	private String id;
	private String name;
	
	// CONSTRUCTOR
	
	public ReciclingPlant(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	// GETTERS & SETTERS
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
