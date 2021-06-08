package com.web.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.demo.model.Appointment;
import com.web.demo.model.User;
import com.web.demo.repo.AppointmentRepository;
import com.web.demo.repo.UserRepository;

@Service
public class AppointmentService {
	
	@Autowired
	private AppointmentRepository repo;
	
	@Autowired
	private UserRepository userRepo;
	
	public void saveAppointment(Appointment appointment, Long doctor_id, Long patient_id) {
		String name,department;
		User doctor=userRepo.findUserById(doctor_id);
		name=doctor.getFirstname()+" "+doctor.getLastname();
		department=userRepo.getDepartmentOfDoctor(doctor_id);
		appointment.setName(name);
		appointment.setDepartment(department);
		User patient=userRepo.findUserById(patient_id);
		String patientname,gender;
		int patientage;
		patientname=patient.getFirstname()+" "+patient.getLastname();
		patientage=patient.getAge();
		gender=patient.getGender();
		appointment.setPatient_gender(gender);
		appointment.setPatient_name(patientname);
		appointment.setPatient_age(patientage);
		appointment.setStatus("Pending");
		repo.save(appointment);
	}
	
	public List<Appointment> pastAppointments(Long id, LocalDateTime d){
		return repo.findPastByPatientId(id,d);
	}
	public List<Appointment> upcomingAppointments(Long id, LocalDateTime d){
		return repo.findUpcomingByPatientId(id,d);
	}
	public List<Appointment> pastDoctorAppointments(Long id, LocalDateTime d){
		return repo.findPastByDoctorId(id,d);
	}
	public List<Appointment> upcomingDoctorAppointments(Long id, LocalDateTime d){
		return repo.findUpcomingByDoctorId(id,d);
	}
	public List<Appointment> allPatientDetails(Long id){
		List<Long> patients=repo.getAllPatients(id);
		List<Appointment> list=new ArrayList<>();
		for(Long p:patients) {
			Appointment a=repo.findPatient(id, p);
			list.add(a);
		}
		return list;
	}
	public void deleteAppointment(Long id) {
		repo.deleteById(id);
	}
}
