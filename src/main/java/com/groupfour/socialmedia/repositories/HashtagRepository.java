package com.groupfour.socialmedia.repositories;

import com.groupfour.socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.Hashtag;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>{

    Optional<Hashtag> findByLabel(String label);

}
