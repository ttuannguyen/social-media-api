package com.groupfour.socialmedia.entities;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
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
	
	@CreationTimestamp
	private Timestamp firstUsed;
	
	@CreationTimestamp
	private Timestamp lastUsed;
	
	@ManyToOne
	@JoinTable(name="tweet_hashtags")
	private Tweet tweet;

}
