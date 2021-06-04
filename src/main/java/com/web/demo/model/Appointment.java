package com.web.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "appointments")
public class Appointment {
	@Id
	@Column(name = "appointment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "patient_id", nullable = false)
	private Long patient_id;
	@Column(name="patient_name", nullable=false, length=45)
	private String patient_name;
	@Column(name="patient_age", nullable=false)
	private int patient_age;
	@Column(name="patient_gender", nullable=false)
	private String patient_gender;
	@Column(name = "doctor_id", nullable = false)
	private Long doctor_id;
	@Column(name="doctor", nullable=false, length=45)
	private String name;
	@Column(name="department", nullable=false, length=45)
	private String department;
	@Column(name = "symptoms", nullable = false, length = 200)
	private String symptoms;
	@Column(name = "status", nullable = false, length = 200)
	private String status;
	@Column(name = "date", nullable = false, length = 100)
	// @Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime date;

	public Appointment() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPatient_id() {
		return patient_id;
	}

	public void setPatient_id(Long patient_id) {
		this.patient_id = patient_id;
	}

	public Long getDoctor_id() {
		return doctor_id;
	}

	public void setDoctor_id(Long doctor_id) {
		this.doctor_id = doctor_id;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPatient_name() {
		return patient_name;
	}

	public void setPatient_name(String patient_name) {
		this.patient_name = patient_name;
	}

	public int getPatient_age() {
		return patient_age;
	}

	public void setPatient_age(int patient_age) {
		this.patient_age = patient_age;
	}

	public String getPatient_gender() {
		return patient_gender;
	}

	public void setPatient_gender(String patient_gender) {
		this.patient_gender = patient_gender;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

}
