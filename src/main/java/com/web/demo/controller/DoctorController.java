package com.web.demo.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.web.demo.model.Appointment;
import com.web.demo.model.Prescription;
import com.web.demo.model.User;
import com.web.demo.repo.AppointmentRepository;
import com.web.demo.repo.PrescriptionRepository;
import com.web.demo.repo.UserRepository;
import com.web.demo.service.AppointmentService;
import com.web.demo.service.UserService;

@Controller
public class DoctorController {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserService service;
	
	@Autowired
	private AppointmentService bookService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PrescriptionRepository prescriptionRepo;
	
	@Autowired
	private AppointmentRepository appointmentRepo;
	
	@GetMapping("/doctor/dashboard")
	public String doctorDashboard(Principal principal, Model model) {
		Long id=service.returnId(principal);
		model.addAttribute("id",id);
		return "Dashboard-D";
	}
	
	@GetMapping("/doctor/appointment-history")
	public String doctorAppointmentHistory(Principal principal, Model model) {
		Long id=service.returnId(principal);
		LocalDateTime dateTime = LocalDateTime.now();
		List<Appointment> past=bookService.pastDoctorAppointments(id, dateTime);
		List<Appointment> upcoming=bookService.upcomingDoctorAppointments(id, dateTime);
		model.addAttribute("pastAppointments",past);
		model.addAttribute("upcomingAppointments",upcoming);
		return "DAppointmentHistory";
	}
	
	@PostMapping("/doctor/change-appointment-status/{id}")
	public String changeStatus(@PathVariable(name="id") Long id, HttpServletRequest request) {
		String status=request.getParameter("status");
		Appointment appointment=appointmentRepo.findByAppointmentId(id);
		appointment.setStatus(status);
		appointmentRepo.save(appointment);
		return "DAppointmentHistory";
	}
	
	@GetMapping("/doctor/patients")
	public String patients(Model model, Principal principal) {
		Long id=service.returnId(principal);
		List<Appointment> patients=bookService.allPatientDetails(id);
		model.addAttribute("patients",patients);
		return "Patients";
	}
	
	@GetMapping("/doctor/add-prescription/{id}")
	public String prescriptionAdd(Model model, @PathVariable(name="id") Long id) {
		Prescription prescription=prescriptionRepo.findByAppointmentID(id);	
		boolean flag=true;
		if(prescription==null) 
			flag=false;
		model.addAttribute("flag", flag);
		model.addAttribute("id", id);
		model.addAttribute("prescription",new Prescription(id));
					
		return "AddPrescription";
	}
	
	@PostMapping("/doctor/add-prescription")
	public String savePrescription(Prescription prescription, HttpServletRequest request) throws ParseException, MessagingException, UnsupportedEncodingException{
		prescriptionRepo.save(prescription);
		Long appointment_id=prescription.getAppointment_id();
		Appointment appointment=appointmentRepo.findByAppointmentId(appointment_id);
		Long patientId=appointment.getPatient_id();
		User user=userRepo.findUserById(patientId);
		//send out an email
		
		String email=user.getEmail();
		String name=appointment.getPatient_name();
		String doctor = appointment.getName();
		String department = appointment.getDepartment();
		String datetime=request.getParameter("prescription_date");
		String nextVisitDate=request.getParameter("nextvisit_date");
		String date=datetime.substring(0, 10);
		String a_id=request.getParameter("appointment_id");
				
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		String mailSubject = "Patient ID "+patientId+" : Your prescription for appointment ID - "+a_id+" is here!";
		String mailContent = "<p>Dear<b> " + name + ",</b></p>";
		mailContent+="<p>Your prescription for your appointment with <b>" + doctor + ", "+department+"</b> on <b>"+date+
				" </b>has been generated! Please login to view it.</p>";	
		mailContent+="<p><b>Suggested next appointment date is</b> "+nextVisitDate+".</p>";
		mailContent+="<a href='localhost:8080'>Login</a>";
		
		helper.setFrom("vanshikaagrawal56@gmail.com", "Virtual Hospital");
		helper.setTo(email);
		helper.setSubject(mailSubject);
		helper.setText(mailContent, true);
		
		mailSender.send(message);
		
		return "DAppointmentHistory";
	}
}
