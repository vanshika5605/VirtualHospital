package com.web.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.web.demo.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long>{
	@Query("SELECT d FROM Department d WHERE d.name=?1")
	public Department findByDepartment(String name);
}
