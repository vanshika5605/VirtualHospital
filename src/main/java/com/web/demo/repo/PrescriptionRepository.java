package com.web.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.demo.model.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long>{
	
	@Query("SELECT p FROM Prescription p WHERE p.appointment_id=?1")
	Prescription findByAppointmentID(Long id);
	
	@Query("SELECT p FROM Prescription p WHERE p.appointment_id IN (SELECT a.id FROM Appointment a WHERE a.patient_id=:id) ORDER BY p.prescription_date")
	List<Prescription> getAllPrescriptionsForAPatient(@Param("id")Long id);
	
	@Query("SELECT p FROM Prescription p WHERE p.appointment_id IN (SELECT a.id FROM Appointment a WHERE a.patient_id=:id) AND p.id "
			+ "NOT IN (SELECT o.prescription_id FROM MedicineOrder o WHERE o.patient_id=:id)")
	List<Prescription> orderNotPlaced(@Param("id") Long p_id);
	
	@Query("SELECT p.medicines FROM Prescription p WHERE id=?1")
	String getMedicines(Long id);
}
