package com.ecoembes.dto;

import java.util.List;

public class AssigmentRequestDTO {
	private int recyclingPlantId;
	private List<Integer> dumpsterIds;
	
	//CONSTRUCTOR
	
	public AssigmentRequestDTO(int recyclingPlantId, List<Integer> dumpsterIds) {
		super();
		this.recyclingPlantId = recyclingPlantId;
		this.dumpsterIds = dumpsterIds;
	}
	
	//GETTERS AND SETTERS

	public int getRecyclingPlantId() {
		return recyclingPlantId;
	}

	public void setRecyclingPlantId(int recyclingPlantId) {
		this.recyclingPlantId = recyclingPlantId;
	}

	public List<Integer> getDumpsterIds() {
		return dumpsterIds;
	}

	public void setDumpsterIds(List<Integer> dumpsterIds) {
		this.dumpsterIds = dumpsterIds;
	}
	
	
}
