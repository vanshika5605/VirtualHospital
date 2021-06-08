package com.web.demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.web.demo.model.Appointment;
import com.web.demo.model.DoctorDepartment;
import com.web.demo.model.Document;
import com.web.demo.model.MedicineOrder;
import com.web.demo.model.Prescription;
import com.web.demo.model.User;
import com.web.demo.model.Vital;
import com.web.demo.repo.DocumentRepository;
import com.web.demo.repo.MedicineOrderRepository;
import com.web.demo.repo.PrescriptionRepository;
import com.web.demo.repo.UserRepository;
import com.web.demo.repo.VitalRepository;
import com.web.demo.service.AppointmentService;
import com.web.demo.service.UserService;

@Controller
public class PatientController {

	@Autowired
	private UserService service;

	@Autowired
	private AppointmentService bookService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PrescriptionRepository prescriptionRepo;

	@Autowired
	private MedicineOrderRepository medicineRepo;
	
	@Autowired
	private DocumentRepository documentRepo;
	
	@Autowired
	private VitalRepository vitalRepo;

	@GetMapping("/patient/dashboard")
	public String patientDashboard(Principal principal, Model model) {
		Long id = service.returnId(principal);
		model.addAttribute("id", id);
		return "Dashboard-P";
	}

	@GetMapping("/patient/book-appointment")
	public String bookAppointment(Principal principal, Model model) {
		List<DoctorDepartment> d = service.createList();
		model.addAttribute("listDoctors", d);
		Long id = service.returnId(principal);
		model.addAttribute("id", id);
		Appointment a = new Appointment();
		a.setPatient_id(id);
		model.addAttribute("appointment", a);

		return "BookAppointment";
	}

	@PostMapping("/patient/book-appointment")
	public RedirectView processAppointment(Appointment appointment, @RequestParam("doctor_id") Long id,
			HttpServletRequest request, Principal principal) throws MessagingException, UnsupportedEncodingException {
		Long p_id = service.returnId(principal);
		bookService.saveAppointment(appointment, id, p_id);
		// send out an email

		String email = principal.getName();
		String name = service.returnName(principal);
		String symptoms = request.getParameter("symptoms");
		String doctor = userRepo.findFirstName(id) + " " + userRepo.findLastName(id);
		String department = userRepo.getDepartmentOfDoctor(id);
		String datetime = request.getParameter("date");
		String date = datetime.substring(0, 10);
		String time = datetime.substring(11);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		String mailSubject = "Patient ID " + p_id + " : Your appointment has been booked!";
		String mailContent = "<p>Dear<b> " + name + ",</b></p>";
		mailContent += "<p><b>Your appointment details are as follows: </b></p>";
		mailContent += "<p><b>Symptoms presented: </b>" + symptoms + "</p>";
		mailContent += "<p><b>Doctor: </b>" + doctor + "</p>";
		mailContent += "<p><b>Department: </b>" + department + "</p>";
		mailContent += "<p><b>Date of Appointment: </b>" + date + "</p>";
		mailContent += "<p><b>Time of Appointment: </b>" + time + " IST </p>";

		helper.setFrom("vanshikaagrawal56@gmail.com", "Virtual Hospital");
		helper.setTo(email);
		helper.setSubject(mailSubject);
		helper.setText(mailContent, true);

		mailSender.send(message);

		return new RedirectView("/patient/appointment-history", true);
	}

	@GetMapping("/patient/appointment-history")
	public String appointmentHistory(Principal principal, Model model) {
		Long id = service.returnId(principal);
		LocalDateTime dateTime = LocalDateTime.now();
		List<Appointment> past = bookService.pastAppointments(id, dateTime);
		List<Appointment> upcoming = bookService.upcomingAppointments(id, dateTime);
		model.addAttribute("pastAppointments", past);
		model.addAttribute("upcomingAppointments", upcoming);

		return "AppointmentHistory";
	}
	
	@RequestMapping("/patient/appointment/cancel/{id}")
	public RedirectView cancelAppointment(@PathVariable("id") Long id) {
		bookService.deleteAppointment(id);
		return new RedirectView("/patient/appointment-history", true);
	}

	@GetMapping("/patient/records")
	public String records(Model model, Principal principal) {
		Long id = service.returnId(principal);
		List<Prescription> pre = prescriptionRepo.getAllPrescriptionsForAPatient(id);
		model.addAttribute("prescriptions", pre);
		List<Document> docs=documentRepo.findByUserId(id);
		model.addAttribute("documents", docs);
		return "Records";
	}
	
	@GetMapping("/patient/records/upload")
	public String uploadRecord() {
		return "RecordUpload";
	}
	
	@PostMapping("/patient/records/upload")
	public RedirectView uploadRecordToDB(@RequestParam("file") MultipartFile file, Principal principal, HttpServletRequest request) {
		Document doc=new Document();
		Long id = service.returnId(principal);
		doc.setUser_id(id);
		doc.setDetails(request.getParameter("details"));
		doc.setDoc_name(request.getParameter("recommended_by"));
		doc.setDate(request.getParameter("date"));
		String fileName = id+"_"+StringUtils.cleanPath(file.getOriginalFilename());
		doc.setDoc_name(fileName);
		try {
			doc.setFile(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		documentRepo.save(doc);
		return new RedirectView("/patient/records", true);
	}
	
	@GetMapping("/download/{fileName:.+}/db")
	public ResponseEntity downloadFromDB(@PathVariable String fileName) {
		Document document = documentRepo.findByDocName(fileName);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(document.getFile());
	}
	
	@RequestMapping("/delete/{fileName:.+}/db")
	public RedirectView deleteFromDB(@PathVariable String fileName) {
		Document document = documentRepo.findByDocName(fileName);
		Long id=document.getId();
		documentRepo.deleteById(id);
		return new RedirectView("/patient/records", true);
	}

	@GetMapping("/patient/doctors-available")
	public String doctors(Model model) {
		List<DoctorDepartment> list = service.createList();
		model.addAttribute("listDoctors", list);
		return "Doctors";
	}

	@GetMapping("/patient/pharmacy")
	public String pharmacy(Principal principal, Model model) {
		Long id = service.returnId(principal);
		User user = userRepo.findUserById(id);
		List<Prescription> pre = prescriptionRepo.orderNotPlaced(id);
		model.addAttribute("user", user);
		model.addAttribute("prescription", pre);

		MedicineOrder order = new MedicineOrder();
		order.setPatient_id(id);
		model.addAttribute("order", order);
		return "Pharmacy";
	}

	@PostMapping("/patient/pharmacy/place-order")
	public RedirectView placeOrder(MedicineOrder order) throws MessagingException, UnsupportedEncodingException {
		LocalDate d = LocalDate.now();
		LocalTime t = LocalTime.now();
		order.setDate(d);
		order.setTime(t);
		order.setStatus("Placed");
		medicineRepo.save(order);

		Long id = order.getPatient_id();
		User user = userRepo.findUserById(id);

		// send email confirmation
		String email = user.getEmail();
		String name = user.getFirstname() + " " + user.getLastname();
		String address = user.getAddress();
		String contact = user.getContact();
		Long order_id = order.getId();
		Long prescription_id = order.getPrescription_id();
		String status = order.getStatus();
		String omitted = order.getOmitted();
		String medicines = prescriptionRepo.getMedicines(prescription_id);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		String mailSubject = "Patient ID " + id + " : Your order with ID - " + order_id + " has been placed!";
		String mailContent = "<p>Dear<b> " + name + ",</b></p>";
		mailContent += "<p>Your order details are as follows:</p>";
		mailContent += "<p><b>Prescription ID: </b>" + prescription_id + "</p>";
		mailContent += "<p><b>Placed On: </b>" + d + "</p>";
		mailContent += "<p><b>Placed At: </b>" + t + "</p>";
		mailContent += "<p><b>Medicines prescribed: </b>" + medicines + "</p>";
		mailContent += "<p><b>Medicines to be omitted: </b>" + omitted + "</p>";
		mailContent += "<p><b>Shipping Address: </b>" + address + "</p>";
		mailContent += "<p><b>Contact Number: </b>" + contact + "</p>";
		mailContent += "<p><b>Status: </b>" + status + "</p>";
		mailContent += "<p>You will be notified once your order is shipped.</p>";

		helper.setFrom("vanshikaagrawal56@gmail.com", "Virtual Hospital");
		helper.setTo(email);
		helper.setSubject(mailSubject);
		helper.setText(mailContent, true);

		mailSender.send(message);

		return new RedirectView("/patient/pharmacy/order-history", true);
	}

	@GetMapping("/patient/pharmacy/order-history")
	public String orderHistory(Principal principal, Model model) {
		Long id = service.returnId(principal);
		List<MedicineOrder> orders = medicineRepo.getAllOrdersForAPatient(id);
		model.addAttribute("orders", orders);
		return "OrderHistory";
	}
	
	@RequestMapping("/order/cancel/{id}")
	public RedirectView cancelOrder(@PathVariable("id") Long id) {
		medicineRepo.deleteById(id);
		return new RedirectView("/patient/pharmacy/order-history", true);
	}

	@GetMapping("/patient/vitals")
	public String vitals(Principal principal, Model model) {
		Long id=service.returnId(principal);
		List<Vital> sugars=vitalRepo.getBloodSugars(id);
		model.addAttribute("sugars", sugars);
		
		List<Vital> pressures=vitalRepo.getBloodPressures(id);
		model.addAttribute("pressures", pressures);
		return "Vitals";
	}
	
	@PostMapping("/save/sugar")
	public RedirectView saveSugar(HttpServletRequest request, Principal principal) {
		String details=request.getParameter("meal")+": "+request.getParameter("sugar");
		LocalDateTime now=LocalDateTime.now().withNano(0).withSecond(0);
		Long id=service.returnId(principal);
		
		Vital vital=new Vital(id,"Sugar",details,now);
		vitalRepo.save(vital);
		
		return new RedirectView("/patient/vitals", true);
	}
	@PostMapping("/save/bp")
	public RedirectView saveBP(HttpServletRequest request, Principal principal) {
		String details=request.getParameter("systolic")+"/"+request.getParameter("diastolic")+" mm Hg";
		String t=request.getParameter("date");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		LocalDateTime dateTime = LocalDateTime.parse(t, formatter);
		Long id=service.returnId(principal);
		
		Vital vital=new Vital(id,"Blood Pressure",details,dateTime);
		vitalRepo.save(vital);
		
		return new RedirectView("/patient/vitals", true);
	}
}
