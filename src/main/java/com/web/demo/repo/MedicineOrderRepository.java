package com.web.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.demo.model.MedicineOrder;

@Repository
public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, Long>{
	
	@Query("SELECT o FROM MedicineOrder o WHERE o.patient_id=?1 ORDER BY o.date")
	List<MedicineOrder> getAllOrdersForAPatient(Long id);
}
