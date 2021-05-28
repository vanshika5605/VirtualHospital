package com.web.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.demo.model.BloodDonation;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long>{

}
