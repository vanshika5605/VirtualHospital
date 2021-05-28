package com.web.demo.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.demo.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
	
	@Query("SELECT a FROM Appointment a WHERE a.patient_id= :id AND a.date>:d ORDER BY a.date ASC")
	List<Appointment> findUpcomingByPatientId(@Param("id") Long id, @Param("d") LocalDateTime t);
	
	@Query("SELECT a FROM Appointment a WHERE a.patient_id= :id AND a.date<:d ORDER BY a.date DESC")
	List<Appointment> findPastByPatientId(@Param("id") Long id, @Param("d") LocalDateTime t);
	
	@Query("SELECT a FROM Appointment a WHERE a.doctor_id= :id AND a.date>:d ORDER BY a.date ASC")
	List<Appointment> findUpcomingByDoctorId(@Param("id") Long id, @Param("d") LocalDateTime t);
	
	@Query("SELECT a FROM Appointment a WHERE a.doctor_id= :id AND a.date<:d ORDER BY a.date DESC")
	List<Appointment> findPastByDoctorId(@Param("id") Long id, @Param("d") LocalDateTime t);
	
	@Query("SELECT DISTINCT a.patient_id FROM Appointment a WHERE a.doctor_id=:id")
	List<Long> getAllPatients(@Param("id") Long id);
	
	@Query(nativeQuery = true, value = "SELECT * FROM appointments WHERE doctor_id=:d_id AND patient_id=:p_id ORDER BY date DESC LIMIT 1")
	Appointment findPatient(@Param("d_id") Long doctor_id, @Param("p_id") Long patient_id);
	
	@Query("SELECT a.name FROM Appointment a WHERE a.id=?1")
	String findDoctor(Long id);
	
	@Query("SELECT a.department FROM Appointment a WHERE a.id=?1")
	String findDepartment(Long id);
}
//