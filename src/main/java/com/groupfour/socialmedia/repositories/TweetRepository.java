package com.groupfour.socialmedia.repositories;

import com.groupfour.socialmedia.entities.Tweet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

	List<Tweet> findAllByDeletedFalseOrderByPostedDesc();

	Optional<Tweet> findByIdAndDeletedFalse(Long id);


}