package com.groupfour.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.*;

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

    private Timestamp posted;

    private String content;

    @JoinColumn(name = "tweet_id")
    private Tweet inReplyTo;

    @JoinColumn(name = "tweet_id")
    private Tweet repostOf;


}
