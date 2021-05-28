package com.web.demo;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.web.demo.model.Role;
import com.web.demo.repo.RoleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace= Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	@Autowired
	RoleRepository repo;
	
	@Test
	public void testCreateRoles() {
		Role patient = new Role("PATIENT");
		Role doctor = new Role("DOCTOR");
		
		repo.saveAll(List.of(patient, doctor));
		
		List<Role> listRoles = repo.findAll();
		assertThat(listRoles.size()).isEqualTo(2);
	}
}
