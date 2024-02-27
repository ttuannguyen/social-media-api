package com.groupfour.socialmedia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
}
