package com.web.demo.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "medicine_order")
public class MedicineOrder {
	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "patient_id", nullable = false)
	private Long patient_id;
	@Column(name = "prescription_id", nullable = false)
	private Long prescription_id;
	@Column(name = "omitted", nullable = false, length = 200)
	private String omitted;
	@Column(name = "placed_date", nullable = false)
	private LocalDate date;
	@Column(name = "placed_time", nullable = false)
	private LocalTime time;
	@Column(name = "status", nullable = false, length = 200)
	private String status;
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
	public Long getPrescription_id() {
		return prescription_id;
	}
	public void setPrescription_id(Long prescription_id) {
		this.prescription_id = prescription_id;
	}
	public String getOmitted() {
		return omitted;
	}
	public void setOmitted(String omitted) {
		this.omitted = omitted;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
}
