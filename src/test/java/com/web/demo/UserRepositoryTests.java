package com.web.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.web.demo.model.Role;
import com.web.demo.model.User;
import com.web.demo.repo.RoleRepository;
import com.web.demo.repo.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
    private TestEntityManager entityManager;
     
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
//    @Test
//    public void testCreateUser() {
//        User user = new User();
//        user.setEmail("ravikumar@gmail.com");
//        user.setPassword("ravi2020");
//        user.setFirstname("Ravi");
//        user.setLastname("Kumar");
//        user.setAddress("Bhopal");
//        user.setBloodgroup("B+");
//        user.setAge(21);
//        user.setContact("7489269695");
//        
//         
//        User savedUser = userRepository.save(user);
//         
//        User existUser = entityManager.find(User.class, savedUser.getId());
//         
//        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
//         
//    }
    
//    @Test
//    public void testAddRoleToNewUser() {
//    	User user = new User();
//        user.setEmail("vritti.agrawal21@gmail.com");
//        user.setPassword("1234");
//        user.setFirstname("Vritti");
//        user.setLastname("Ag");
//        user.setAddress("Bhopal");
//        user.setBloodgroup("B+");
//        user.setAge(14);
//        user.setContact("9329988157");
//        
//        Role roleUser = roleRepository.findByName("PATIENT");
//        user.addRole(roleUser);
//        
//        User savedUser = userRepository.save(user);
//        
//        assertThat(savedUser.getRoles().size()).isEqualTo(1);
//    }
    
    @Test
//    public void testAddRolesToExistingUser() {
//    	Set<Role> roles = new HashSet<>();
//    	Role roleUser = roleRepository.findByName("DOCTOR");
//    	roles.add(roleUser);
//    	
//    	for(Role r:roles)
//    		System.out.println(r.getName());
//    	//System.out.println(roles);
//    	List<User> users= userRepository.findByRole("DOCTOR");
//    	System.out.println(users);
//    	for(User u:users)
//    		System.out.println(u.getFirstname());
//    	
//    	String d=userRepository.getDepartmentOfDoctor(18L);
//    	System.out.println(d);
//    	
//    	//User user = userRepository.findById(16L).get();
//    	
//    	
//    	//System.out.println(user.getRoles());
//    	
////    	User user = userRepository.findById(1L).get();
////    	
////    	Role roleUser = roleRepository.findByName("DOCTOR");
////        user.addRole(roleUser);
////        
////        User savedUser = userRepository.save(user);
////        
////        assertThat(savedUser.getRoles().size()).isEqualTo(1);
//    }
    public void test() {
    	LocalDateTime dateTime = LocalDateTime.now();
    	System.out.println(dateTime);
    }
}
