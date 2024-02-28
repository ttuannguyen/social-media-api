package com.groupfour.socialmedia.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	public Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);

	public Optional<User> findByCredentialsUsername(String username);
	
	
}
