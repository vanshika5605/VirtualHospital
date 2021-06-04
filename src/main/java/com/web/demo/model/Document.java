package com.web.demo.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "documents")
public class Document {
	@Id
	@Column(name = "document_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long user_id;

	@Column(name = "doc_name", nullable = false)
	private String doc_name;

	@Column(name = "details", nullable = true)
	private String details;

	@Column(name = "date", nullable = true)
	private String date;

	@Column(name = "recommended_by", nullable = true)
	private String recommended_by;

	@Column(name = "file", nullable = false)
	@Lob
	private byte[] file;

	
	
	public Document() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRecommended_by() {
		return recommended_by;
	}

	public void setRecommended_by(String recommended_by) {
		this.recommended_by = recommended_by;
	}

}
