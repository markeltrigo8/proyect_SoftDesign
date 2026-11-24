package com.ecoembes.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecoembes.dao.EmployeeRepository;
import com.ecoembes.domain.Employee;

@Service
public class AuthService {

	private final EmployeeRepository employeeRepository;

	// token -> email del empleado. Se guarda el email (no el objeto) para que el
	// Employee se cargue siempre desde la BD y no quede "suelto" entre peticiones.
	private final Map<String, String> tokenRepository = new ConcurrentHashMap<>();

	public AuthService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Transactional
	public void addEmployee(Employee employee) {
		employeeRepository.save(employee);
	}

	@Transactional(readOnly = true)
	public Optional<String> login(String email, String password) {
		if (email == null || password == null) {
			return Optional.empty();
		}

		Optional<Employee> employee = employeeRepository.findById(email);

		if (employee.isPresent() && employee.get().checkPassword(password)) {
			String token = generateToken();
			tokenRepository.put(token, email);
			return Optional.of(token);
		}
		return Optional.empty();
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

	@Transactional(readOnly = true)
	public Optional<Employee> getEmployeeByToken(String token) {
		if (token == null) {
			return Optional.empty();
		}
		String email = tokenRepository.get(token);
		if (email == null) {
			return Optional.empty();
		}
		return employeeRepository.findById(email);
	}

}
