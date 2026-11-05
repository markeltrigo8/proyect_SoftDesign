package com.ecoembes.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class AssigmentDTO {
	private LocalDate date;
	private int recyclingPlantId;
	private String employeeEmail;
	private List<Integer> dumpstersId;
	private int totalContainers;
	private int totalDumpsters;
	
	//CONSTRUCTOR
	
	public AssigmentDTO(LocalDate date, int recyclingPlantId, String employeeEmail, List<Integer> dumpstersId,
			int totalContainers, int totalDumpsters) {
		super();
		this.date = date;
		this.recyclingPlantId = recyclingPlantId;
		this.employeeEmail = employeeEmail;
		this.dumpstersId = dumpstersId;
		this.totalContainers = totalContainers;
		this.totalDumpsters = totalDumpsters;
	}
	
	//GETTERS Y SETTERS
	
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public int getRecyclingPlantId() {
		return recyclingPlantId;
	}
	public void setRecyclingPlantId(int recyclingPlantId) {
		this.recyclingPlantId = recyclingPlantId;
	}
	public String getEmployeeEmail() {
		return employeeEmail;
	}
	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}
	public List<Integer> getContainersId() {
		return dumpstersId;
	}
	public void setContainersId(List<Integer> containersId) {
		this.dumpstersId = containersId;
	}
	public int getTotalContainers() {
		return totalContainers;
	}
	public void setTotalContainers(int totalContainers) {
		this.totalContainers = totalContainers;
	}
	public int getTotalDumpsters() {
		return totalDumpsters;
	}
	public void setTotalDumpsters(int totalDumpsters) {
		this.totalDumpsters = totalDumpsters;
	}
	@Override
	public int hashCode() {
		return Objects.hash(dumpstersId, date, employeeEmail);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssigmentDTO other = (AssigmentDTO) obj;
		return Objects.equals(dumpstersId, other.dumpstersId) && Objects.equals(date, other.date)
				&& Objects.equals(employeeEmail, other.employeeEmail);
	}
	
	
}
