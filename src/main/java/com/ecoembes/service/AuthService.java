package com.ecoembes.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ecoembes.domain.Employee;

@Service
public class AuthService {
	private Map<String, Employee> employeeRepository = new ConcurrentHashMap<>();
	private Map<String, Employee> tokenRepository = new ConcurrentHashMap<>();
	
	public void addEmployee(Employee employee) {
		employeeRepository.put(employee.getEmail(), employee);
	}
	
	public Optional<String> login(String email, String password ) {
		if (email ==null || password == null) {
			return Optional.empty();
		}
		
		Employee employee = employeeRepository.get(email);
		
		if (employee != null && employee.checkPassword(password)) {
			String token = generateToken();
			tokenRepository.put(token, employee);
			
			return Optional.of(token);
		}else {
			return Optional.empty();
		}
	}
	
	public Optional<Boolean> logout(String token) {
		if (token == null || !tokenRepository.containsKey(token)) {
			return Optional.empty();
		}
		
		tokenRepository.remove(token);
		return Optional.of(true);
	}

	private static String generateToken() {
		return UUID.randomUUID().toString();
	}


	public Optional<Employee> getEmployeeByToken(String token) {
		if (token == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(tokenRepository.get(token));
	}
	
}
