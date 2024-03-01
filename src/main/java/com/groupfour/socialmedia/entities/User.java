package com.groupfour.socialmedia.entities;



import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name="userTable")
public class User {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Embedded
	private Credentials credentials;
	
	@Embedded
	private Profile profile;
	
	@CreationTimestamp
	private Timestamp joined;
	
	private boolean deleted = false;
	
	@ManyToMany
	@JoinTable(name="followers_following")
	private List<User> followers = new ArrayList<>();
	
	@ManyToMany(mappedBy="followers")
	private List<User> following = new ArrayList<>();
	
	@OneToMany(mappedBy="author")
	private List<Tweet> tweets;
	
	@ManyToMany
	@JoinTable(name="user_likes", 
	joinColumns = @JoinColumn(name="user_id"),
	inverseJoinColumns = @JoinColumn(name="tweet_id"))
	private List<Tweet> likedTweets = new ArrayList<>();
	
	@ManyToMany(mappedBy="mentionedUsers")
	private List<Tweet> mentions = new ArrayList<>();
	

	public boolean getDeleted() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
}
