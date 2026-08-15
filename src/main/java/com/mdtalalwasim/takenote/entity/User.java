package com.mdtalalwasim.takenote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotBlank(message = "Name cannot be empty")
	private String name;
	
	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Invalid email format")
	@Column(unique = true, length = 150)
	private String email;
	
	@Column(length = 255)
	private String address;
	
	@Column(length = 20)
	private String phone;
	
	@Column(length = 10)
	private String gender;
	
	@NotBlank(message = "Password cannot be empty")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Column(length = 255)
	private String password;
	
	@Column(length = 20)
	private String role;
	
	@Column(columnDefinition = "boolean default true")
	private boolean enabled = true;

}
