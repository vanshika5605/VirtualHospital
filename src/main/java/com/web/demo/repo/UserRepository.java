package com.web.demo.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.demo.model.Role;
import com.web.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	@Query("SELECT u FROM User u WHERE u.email=?1")
	User findByEmail(String email);
	
	@Query("SELECT u FROM User u JOIN u.roles r WHERE r.id IN (SELECT ur.id FROM Role ur WHERE ur.name = :role)")
	public List<User> findByRole(@Param("role") String userRole);
	
	@Query("SELECT dep.name FROM Department dep WHERE dep.id IN (SELECT d.id FROM User u JOIN u.departments d WHERE u.id= :id)")
	String getDepartmentOfDoctor(@Param("id")Long id);
	
	@Query("SELECT u.id FROM User u WHERE u.email= :email")
	Long getUserId(@Param("email") String email);
	
	@Query("SELECT u FROM User u WHERE u.id=?1")
	User findUserById(Long id);
	
	@Query("SELECT u.firstname FROM User u WHERE u.id=?1")
	String findFirstName(Long id);
	
	@Query("SELECT u.lastname FROM User u WHERE u.id=?1")
	String findLastName(Long id);
	
	@Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id=?1")
	String findRoleOfUser(Long id);
}
