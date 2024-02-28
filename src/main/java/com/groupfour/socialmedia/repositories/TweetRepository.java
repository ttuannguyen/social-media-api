package com.groupfour.socialmedia.repositories;

import com.groupfour.socialmedia.entities.Tweet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Optional<Tweet> findByIdAndDeletedFalse(Long id);

}