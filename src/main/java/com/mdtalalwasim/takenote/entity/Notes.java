package com.mdtalalwasim.takenote.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Where(clause = "deleted=false")
@Entity
public class Notes {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotBlank(message = "Title cannot be empty")
	@Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
	private String title;
	
	@NotBlank(message = "Description cannot be empty")
	@Size(min = 10, message = "Description must be at least 10 characters long")
	private String description;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDate createdAt;
	
	@UpdateTimestamp
	private LocalDate updatedAt;

	@Column(columnDefinition = "boolean default false")
	private boolean favorite;

	@Column(columnDefinition = "boolean default false")
	private boolean deleted;
	
	@ManyToOne
	private User user;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "note_tag",
			joinColumns = @JoinColumn(name = "note_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags = new java.util.HashSet<>();

}
