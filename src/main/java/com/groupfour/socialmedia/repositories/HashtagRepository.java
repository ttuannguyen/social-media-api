package com.groupfour.socialmedia.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>{


    Optional<Hashtag> findByLabel(String label);


}
