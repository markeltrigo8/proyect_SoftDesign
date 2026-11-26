package com.plas.dto;

// Respuesta de GET /capacity -> {"availableCapacity": 85}
public class CapacityDTO {

	private int availableCapacity;

	public CapacityDTO() {
	}

	public CapacityDTO(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}

	public int getAvailableCapacity() {
		return availableCapacity;
	}

	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}
}