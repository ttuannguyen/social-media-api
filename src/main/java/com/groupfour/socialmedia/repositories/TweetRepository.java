package com.groupfour.socialmedia.repositories;

import com.groupfour.socialmedia.entities.Tweet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// You may think you don't need this Repository, but remember each Repository interface
// only allows you to interact with the 1 table it maps to, so in order to save or retrieve
// questions directly you need to use this interface. You can still access Questions stored on a Quiz
// without using this interface.
@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {



}