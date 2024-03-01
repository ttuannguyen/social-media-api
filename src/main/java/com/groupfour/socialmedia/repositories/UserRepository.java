package com.groupfour.socialmedia.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByCredentialsUsername(String username);

	Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);
	
	// created to assist the createLike() method in TweetServiceImpl
	Optional<User> findByCredentials(Credentials credentials);

	Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);
	
}
