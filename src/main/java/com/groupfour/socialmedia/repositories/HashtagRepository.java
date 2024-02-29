package com.groupfour.socialmedia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>{

	// Method to check if a hashtag with a specific label exists in the DB
	Hashtag findByLabel(String label);

}
