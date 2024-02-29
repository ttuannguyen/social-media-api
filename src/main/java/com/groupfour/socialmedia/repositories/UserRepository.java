package com.groupfour.socialmedia.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);

	Optional<User> findByCredentialsUsername(String username);

	List<User> findAllByDeletedFalse();

	Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);

}
