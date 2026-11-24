package com.ecoembes.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoembes.domain.Employee;

// El email es la clave primaria, así que findById(email) ya sirve para buscar por email
public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
