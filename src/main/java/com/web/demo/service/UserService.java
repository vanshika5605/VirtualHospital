package com.web.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.demo.model.Role;
import com.web.demo.model.User;
import com.web.demo.model.Department;
import com.web.demo.model.DoctorDepartment;
import com.web.demo.repo.DepartmentRepository;
import com.web.demo.repo.RoleRepository;
import com.web.demo.repo.UserRepository;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private DepartmentRepository depRepo;
	
	public void saveUser(User user, String role, String department) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword=passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		Role roleUser = roleRepo.findByName(role);
		user.addRole(roleUser);
		
		if(department!=null) {
		Department dep = depRepo.findByDepartment(department);
		user.addDepartment(dep);}
		
		Date dateOfBirth=user.getBirthday();
		LocalDate d=dateOfBirth.toLocalDate();
		Period period = Period.between(d, LocalDate.now());
		user.setAge(period.getYears());
		
		userRepo.save(user);
	}
	
	public List<User> listDoctors(){
		return userRepo.findByRole("DOCTOR");
	}
	
	public List<DoctorDepartment> createList(){
		List<User> listDoctors = userRepo.findByRole("DOCTOR");
		List<DoctorDepartment> ans = new ArrayList<>();
		for(User u:listDoctors) {
			Long id=u.getId();
			String name, department, photo,bio,meetId;
			name=u.getFirstname()+" "+u.getLastname();
			department = userRepo.getDepartmentOfDoctor(id);
			photo=u.getPhoto();
			bio=u.getBio();
			meetId=u.getEmail();
			DoctorDepartment d=new DoctorDepartment(id,name,department,photo,bio,meetId);
			ans.add(d);
		}
		return ans;
	}
	public Long returnId(Principal principal) {
		Long id=userRepo.getUserId(principal.getName());
		return id;
	}
	
	public String returnName(Principal principal) {
		Long id=userRepo.getUserId(principal.getName());
		String f=userRepo.findFirstName(id);
		String l=userRepo.findLastName(id);
		return f+" "+l;
	}
}
