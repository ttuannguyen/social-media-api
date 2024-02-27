package com.groupfour.socialmedia.entities;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Hashtag {
	
	@Id
	@GeneratedValue
	private Long id;
	private String label;
	private Timestamp firstUsed;
	private Timestamp lastUsed;

}