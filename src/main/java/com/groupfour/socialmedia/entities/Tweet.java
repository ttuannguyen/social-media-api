package com.groupfour.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

    private Timestamp posted;

    private String content;

    @JoinColumn(name = "tweet_id")
    private Tweet inReplyTo;

    @JoinColumn(name = "tweet_id")
    private Tweet repostOf;
	
	@ManyToMany(mappedBy="likedTweets")
	private List<User> likedByUsers = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name="user_mentions", 
	joinColumns = @JoinColumn(name="tweet_id"),
	inverseJoinColumns = @JoinColumn(name="user_id"))
	private List<User> mentionedUsers = new ArrayList<>();

}
