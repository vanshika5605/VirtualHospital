package com.web.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.web.demo.model.Vital;

public interface VitalRepository extends JpaRepository<Vital, Long>{
	
	@Query("SELECT v FROM Vital v WHERE v.patient_id=?1 AND v.type='Sugar'")
	List<Vital> getBloodSugars(Long id);
	
	@Query("SELECT v FROM Vital v WHERE v.patient_id=?1 AND v.type='Blood Pressure'")
	List<Vital> getBloodPressures(Long id);
}
