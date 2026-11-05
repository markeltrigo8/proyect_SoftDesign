package com.ecoembes.dto;

import java.util.Objects;

public class CredentialsDTO {
	private String email;
	private String password;
	
	//CONSTRUCTOR
	
	CredentialsDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	//GETTERS AND SETTERS

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

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CredentialsDTO other = (CredentialsDTO) obj;
		return Objects.equals(email, other.email);
	}
	
	
}
