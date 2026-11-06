package com.ecoembes.dto;

public class DumpsterUpdateDTO {
	private int dumpsterId;
	private int numContainers;
	private String fillLevel;
	
	//CONSTRUCTOR
	
	public DumpsterUpdateDTO(int dumpsterId, int numContainers, String fillLevel) {
		super();
		this.dumpsterId = dumpsterId;
		this.numContainers = numContainers;
		this.fillLevel = fillLevel;
	}

	//GETTERS AND SETTERS
	
	public int getDumpsterId() {
		return dumpsterId;
	}

	public void setDumpsterId(int dumpsterId) {
		this.dumpsterId = dumpsterId;
	}

	public int getNumContainers() {
		return numContainers;
	}

	public void setNumContainers(int numContainers) {
		this.numContainers = numContainers;
	}

	public String getFillLevel() {
		return fillLevel;
	}

	public void setFillLevel(String fillLevel) {
		this.fillLevel = fillLevel;
	}
	

	
}
