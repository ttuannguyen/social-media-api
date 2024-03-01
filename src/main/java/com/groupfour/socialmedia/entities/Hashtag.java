package com.groupfour.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
	

	@UpdateTimestamp
	private Timestamp lastUsed;


	@ManyToMany
	@JoinTable(name="tweet_hashtags",
			joinColumns = @JoinColumn(name="hashtag_id"),
			inverseJoinColumns = @JoinColumn(name="tweet_id"))
	
	private List<Tweet> taggedTweets;

}
