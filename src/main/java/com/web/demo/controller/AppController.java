package com.web.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.web.demo.model.User;
import com.web.demo.model.BloodDonation;
import com.web.demo.model.Prescription;
import com.web.demo.repo.AppointmentRepository;
import com.web.demo.repo.BloodDonationRepository;
import com.web.demo.repo.PrescriptionRepository;
import com.web.demo.repo.UserRepository;
import com.web.demo.service.UserService;

@Controller
public class AppController {
	
	@Autowired
	private UserService service;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BloodDonationRepository repo;
	
	@Autowired
	private PrescriptionRepository prescriptionRepo;
	
	@Autowired
	private AppointmentRepository appointmentRepo;
	
	@GetMapping("/")
	public String viewHomePage() {
		return "Home";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "Login";
	}
	
	@PostMapping("/login")
	public String login() {
		return "Home";
	}
	
	@GetMapping("/logout")
	public String logout() {
		return "Home";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new User());
		
		return "Register";
	}
	@PostMapping("/register")
	public String processRegister(@Valid User user, @RequestParam("role") String role, @RequestParam("department") String department) {
		if (userRepo.existsByEmail(user.getEmail())) {
			return "Error: Email is already in use!";
		}
		
		service.saveUser(user, role, department);
		
		return "Login";
	}
	
	@GetMapping("/accountDetails")
	public String getDetails(Principal principal, Model model) {
		Long id=service.returnId(principal);
		model.addAttribute("id",id);
		User user=userRepo.findUserById(id);
		model.addAttribute("user",user);
		String dep=userRepo.getDepartmentOfDoctor(id);
		model.addAttribute("dep",dep);
		int countDonations=repo.bloodDonations(id);
		model.addAttribute("donations", countDonations);
		return "AccountDetails";
	}
	
	@PostMapping("/save/photo")
    public RedirectView saveUser(Principal principal,
            @RequestParam("image") MultipartFile multipartFile) throws IOException {
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setPhoto(fileName);
         
        User savedUser = userRepo.save(user);
 
        String uploadDir = "user-photos/" + savedUser.getId();
 
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
         
        return new RedirectView("/accountDetails", true);
    }
	
	@PostMapping("/change-password")
	public RedirectView changeUserPassword(HttpServletRequest request, Principal principal) {
		String pass=request.getParameter("password");
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword=passwordEncoder.encode(pass);
		user.setPassword(encodedPassword);
		
		userRepo.save(user);
		return new RedirectView("/accountDetails", true);
	}
	
	@PostMapping("/change-contact")
	public RedirectView changeContact(HttpServletRequest request, Principal principal) {
		String contact=request.getParameter("contact");
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		user.setContact(contact);
		userRepo.save(user);
		return new RedirectView("/accountDetails", true);
	}
	
	@PostMapping("/change-address")
	public RedirectView changeAddress(HttpServletRequest request, Principal principal) {
		String address=request.getParameter("address");
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		user.setAddress(address);
		userRepo.save(user);
		return new RedirectView("/accountDetails", true);
	}
	
	@PostMapping("/change-bio")
	public RedirectView changeBio(HttpServletRequest request, Principal principal) {
		String bio=request.getParameter("bio");
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		user.setBio(bio);
		userRepo.save(user);
		return new RedirectView("/accountDetails", true);
	}
	
	@GetMapping("/helpdesk")
	public String helpdesk(Model model, Principal principal) {
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		String role=userRepo.findRoleOfUser(id);
		String username=user.getFirstname()+" "+user.getLastname()+" ("+role+")";
		
		model.addAttribute("username", username);
		return "Helpdesk";
	}
	
	
	@GetMapping("/bloodDonation")
	public String donation(Principal principal, Model model) {
		Long id=service.returnId(principal);
		String name=service.returnName(principal);
		BloodDonation donation=new BloodDonation(id,name);
		
		model.addAttribute("bloodDonation", donation);
		model.addAttribute("name", name);
		model.addAttribute("id",id);
		return "BloodDonation";
	}
	@PostMapping("/bloodDonation")
	public String giveDonation(@ModelAttribute BloodDonation donation) {
		repo.save(donation);
		return "Home";
	}	
	
	@GetMapping("/viewPrescription/{id}")
	public String viewPrescription(Model model, @PathVariable(name="id") Long id)  {
		Prescription prescription=prescriptionRepo.findByAppointmentID(id);	
		boolean flag=true;
		if(prescription==null)
			flag=false;
		String doctor=appointmentRepo.findDoctor(id)+", "+appointmentRepo.findDepartment(id);
		model.addAttribute("doctor",doctor);
		model.addAttribute("prescription",prescription);
		model.addAttribute("id",id);
		model.addAttribute("flag", flag);
		return "ViewPrescription";
	}	
	
	@GetMapping("/meet")
	public String meeting(Model model, Principal principal) {
		Long id=service.returnId(principal);
		User user=userRepo.findUserById(id);
		model.addAttribute("username", user.getEmail());
		return "Meet";
	}
}
