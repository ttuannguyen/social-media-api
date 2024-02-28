package com.groupfour.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

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

    @CreationTimestamp
    private Timestamp posted;

    private String content;
    
    @ManyToOne
    @JoinColumn(name = "inReplyTo")
    private Tweet inReplyTo;
    
    @ManyToOne
    @JoinColumn(name = "repostOf")
    private Tweet repostOf;
    
    @OneToMany(mappedBy = "inReplyTo")
    private List<Tweet> replies;

    @OneToMany(mappedBy = "repostOf")
    private List<Tweet> reposts;
	
	@ManyToMany(mappedBy="likedTweets")
	private List<User> likedByUsers = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name="user_mentions", 
	joinColumns = @JoinColumn(name="tweet_id"),
	inverseJoinColumns = @JoinColumn(name="user_id"))
	private List<User> mentionedUsers = new ArrayList<>();
	
	private boolean deleted = false;
	
	@OneToMany(mappedBy="taggedTweets")
	private List<Hashtag> hashtags;

}
