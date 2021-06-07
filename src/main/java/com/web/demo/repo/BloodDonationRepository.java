package com.web.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.demo.model.BloodDonation;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long>{
	
	@Query("SELECT COUNT(b) FROM BloodDonation b WHERE b.user_id=?1")
	int bloodDonations(Long id);
}
