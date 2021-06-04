package com.web.demo.model;

import javax.persistence.Column;
import javax.persistence.Transient;

public class DoctorDepartment {
	private Long id;
	private String name;
	private String department;
	@Column(name = "photo", nullable = true, length = 64)
	public String photo;
	@Column(name = "bio", nullable = true, length = 200)
	private String bio;


	public DoctorDepartment(Long id, String name, String department, String photo, String bio) {
		this.id = id;
		this.name = name;
		this.department = department;
		this.photo = photo;
		this.bio = bio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Transient
	public String getPhotosImagePath() {
		if (photo == null || id == null)
			return null;

		return "/user-photos/" + id + "/" + photo;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

}
