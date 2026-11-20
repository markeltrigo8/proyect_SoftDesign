package com.ecoembes.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Employee {
	private String name;
	private String email;
	private String password;
	private List<Assigment> assigments = new CopyOnWriteArrayList<>();
	
	public Employee(String name, String email, String password) {
		super();
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
	public List<Assigment> getEmployeeAsigments(){
		return this.assigments;
	}
	public void setAssigments (List<Assigment> a) {
		this.assigments = a;
	}
}
