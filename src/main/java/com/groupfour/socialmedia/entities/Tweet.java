package com.groupfour.socialmedia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@NoArgsConstructor
@Data
public class Tweet {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User author;
	
	@ManyToMany(mappedBy="likedTweets")
	private List<User> likedByUsers = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name="user_mentions", 
	joinColumns = @JoinColumn(name="tweet_id"),
	inverseJoinColumns = @JoinColumn(name="user_id"))
	private List<User> mentionedUsers = new ArrayList<>();
}
