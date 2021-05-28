package com.web.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "prescriptions")
public class Prescription {
	@Id
	@Column(name = "prescription_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "prescription_date", nullable = false)
	private Date prescription_date;
	@Column(name = "appointment_id", nullable = false)
	private Long appointment_id;
	@Column(name = "diagnosis", nullable = false, length = 200)
	private String diagnosis;
	@Column(name = "medicines", nullable = false, length = 200)
	private String medicines;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "nextvisit_date", nullable = false)
	private Date nextvisit_date;
	
	public Prescription() {
		
	}
	
	public Prescription(Long appointment_id) {
		this.appointment_id = appointment_id;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getPrescription_date() {
		return prescription_date;
	}
	public void setPrescription_date(Date prescription_date) {
		this.prescription_date = prescription_date;
	}
	public Long getAppointment_id() {
		return appointment_id;
	}
	public void setAppointment_id(Long appointment_id) {
		this.appointment_id = appointment_id;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	public String getMedicines() {
		return medicines;
	}
	public void setMedicines(String medicines) {
		this.medicines = medicines;
	}
	public Date getNextvisit_date() {
		return nextvisit_date;
	}
	public void setNextvisit_date(Date nextvisit_date) {
		this.nextvisit_date = nextvisit_date;
	}
	
	
	
}
