package com.ecoembes.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Employee {

	@Id
	private String email;

	private String name;
	private String password;

	@OneToMany(mappedBy = "employee")
	private List<Assigment> assigments = new ArrayList<>();

	// Constructor requerido por JPA
	protected Employee() {
	}

	public Employee(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	public void addAssigment(Assigment a) {
		this.assigments.add(a);
	}

	public List<Assigment> getEmployeeAsigments() {
		return this.assigments;
	}

	public void setAssigments(List<Assigment> a) {
		this.assigments = a;
	}
}
