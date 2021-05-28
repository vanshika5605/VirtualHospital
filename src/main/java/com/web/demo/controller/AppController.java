package com.web.demo.controller;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.web.demo.model.User;
import com.web.demo.model.Appointment;
import com.web.demo.model.BloodDonation;
import com.web.demo.model.DoctorDepartment;
import com.web.demo.model.Prescription;
import com.web.demo.repo.AppointmentRepository;
import com.web.demo.repo.BloodDonationRepository;
import com.web.demo.repo.PrescriptionRepository;
import com.web.demo.repo.UserRepository;
import com.web.demo.service.AppointmentService;
import com.web.demo.service.UserService;

@Controller
public class AppController {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserService service;
	
	@Autowired
	private AppointmentService bookService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BloodDonationRepository repo;
	
	@Autowired
	private PrescriptionRepository prescriptionRepo;
	
	@Autowired
	private AppointmentRepository appointmentRepo;
	
	@GetMapping("")
	public String viewHomePage() {
		return "Home";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "Login";
	}
	
	@PostMapping("/login_success")
	public String loginSuccessHandler() {
		System.out.println("Successfully logged in!");
		return "redirect:/";
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
	public String processRegister(User user, @RequestParam("role") String role, @RequestParam("department") String department) {
		service.saveUser(user, role, department);
		
		return "Login";
	}
	
	@GetMapping("/patientDashboard")
	public String patientDashboard(Principal principal, Model model) {
		Long id=service.returnId(principal);
		model.addAttribute("id",id);
		return "Dashboard-P";
	}
	
	@GetMapping("/patient/bookAppointment")
	public String bookAppointment(Principal principal, Model model) {
		List<DoctorDepartment> d=service.createList();
		model.addAttribute("listDoctors",d);
		Long id=service.returnId(principal);
		model.addAttribute("id",id);
		Appointment a=new Appointment();
		a.setPatient_id(id);
		model.addAttribute("appointment", a);
		
		return "BookAppointment";
	}
	
	@PostMapping("/patient/bookAppointment")
	public String processAppointment(Appointment appointment, @RequestParam("doctor_id") Long id, HttpServletRequest request, Principal principal) 
			throws MessagingException, UnsupportedEncodingException {
		Long p_id=service.returnId(principal);
		bookService.saveAppointment(appointment,id,p_id);
		//send out an email
		
		String email=principal.getName();
		String name=service.returnName(principal);
		String symptoms = request.getParameter("symptoms");
		String doctor = userRepo.findFirstName(id)+" "+userRepo.findLastName(id);
		String department = userRepo.getDepartmentOfDoctor(id);
		String datetime=request.getParameter("date");
		String date=datetime.substring(0, 10);
		String time=datetime.substring(11);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		String mailSubject = "Patient ID "+p_id+" : Your appointment has been booked!";
		String mailContent = "Dear<p><b> " + name + ",</b></p>";
		mailContent+= "<p><b>Your appointment details are as follows: </b></p>";
		mailContent+= "<p><b>Symptoms presented: </b>" + symptoms +"</p>";
		mailContent+= "<p><b>Doctor: </b>" + doctor +"</p>";
		mailContent+= "<p><b>Department: </b>" + department +"</p>";
		mailContent+= "<p><b>Date of Appointment: </b>" + date +"</p>";
		mailContent+= "<p><b>Time of Appointment: </b>" + time +" IST </p>";
		
		helper.setFrom("vanshikaagrawal56@gmail.com", "Virtual Hospital");
		helper.setTo(email);
		helper.setSubject(mailSubject);
		helper.setText(mailContent, true);
		
		mailSender.send(message);
		
		return "Dashboard-P";
	}
	
	@GetMapping("/patient/appointmentHistory")
	public String appointmentHistory(Principal principal, Model model) {
		Long id=service.returnId(principal);
		LocalDateTime dateTime = LocalDateTime.now();
		List<Appointment> past=bookService.pastAppointments(id, dateTime);
		List<Appointment> upcoming=bookService.upcomingAppointments(id, dateTime);
		model.addAttribute("pastAppointments",past);
		model.addAttribute("upcomingAppointments",upcoming);
		
		return "AppointmentHistory";
	}
	@GetMapping("/helpdesk")
	public String helpdesk() {
		return "Helpdesk";
	}
	@GetMapping("/patient/records")
	public String records(Model model, Principal principal) {
		Long id=service.returnId(principal);
		List<Prescription> pre=prescriptionRepo.getAllPrescriptionsForAPatient(id);
		model.addAttribute("prescriptions", pre);
		return "Records";
	}
	@GetMapping("/doctorsAvailable")
	public String doctors(Model model) {
		List<DoctorDepartment> list=service.createList();
		model.addAttribute("listDoctors",list);
		return "Doctors";
	}
	@GetMapping("/pharmacy")
	public String pharmacy() {
		return "Pharmacy";
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
		return "BloodDonation";
	}
	@GetMapping("/doctorDashboard")
	public String doctorDashboard(Principal principal, Model model) {
		Long id=service.returnId(principal);
		model.addAttribute("id",id);
		return "Dashboard-D";
	}
	
	@GetMapping("/doctor/appointmentHistory")
	public String doctorAppointmentHistory(Principal principal, Model model) {
		Long id=service.returnId(principal);
		LocalDateTime dateTime = LocalDateTime.now();
		List<Appointment> past=bookService.pastDoctorAppointments(id, dateTime);
		List<Appointment> upcoming=bookService.upcomingDoctorAppointments(id, dateTime);
		model.addAttribute("pastAppointments",past);
		model.addAttribute("upcomingAppointments",upcoming);
		return "DAppointmentHistory";
	}
	
	@GetMapping("/doctor/patients")
	public String patients(Model model, Principal principal) {
		Long id=service.returnId(principal);
		List<Appointment> patients=bookService.allPatientDetails(id);
		model.addAttribute("patients",patients);
		return "Patients";
	}
	
	@GetMapping("/addPrescription/{id}")
	public String prescriptionAdd(Model model, @PathVariable(name="id") Long id) {
		model.addAttribute("prescription",new Prescription(id));
		return "AddPrescription";
	}
	
	@PostMapping("/addPrescription")
	public String saveProduct(Prescription prescription) throws ParseException{
		Date pdate=prescription.getPrescription_date();
		Date vdate=prescription.getNextvisit_date();
		
		DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
		String output = outputFormatter.format(pdate);
		DateFormat inputFormatter = new SimpleDateFormat("yyyy/MM/dd");
		pdate = inputFormatter.parse(output);
		
		output=outputFormatter.format(vdate);
		vdate=inputFormatter.parse(output);
		
		prescription.setPrescription_date(pdate);
		prescription.setNextvisit_date(vdate);
		
		prescriptionRepo.save(prescription);
		return "AddPrescription";
	}
	
	@GetMapping("/viewPrescription/{id}")
	public String viewPrescription(Model model, @PathVariable(name="id") Long id)  {
		Prescription prescription=prescriptionRepo.findByAppointmentID(id);	
		String doctor=appointmentRepo.findDoctor(id)+", "+appointmentRepo.findDepartment(id);
		model.addAttribute("doctor",doctor);
		model.addAttribute("prescription",prescription);
		model.addAttribute("id",id);
		return "ViewPrescription";
	}
}
