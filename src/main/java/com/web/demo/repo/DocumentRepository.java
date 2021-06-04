package com.web.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.demo.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long>{
	
	@Query("SELECT d FROM Document d WHERE d.doc_name=?1")
	Document findByDocName(String filename);
	
	@Query("SELECT d FROM Document d WHERE d.user_id=?1")
	List<Document> findByUserId(Long id);
}
