package com.groupfour.socialmedia.repositories;

import java.util.List;
import java.util.Optional;

import com.groupfour.socialmedia.entities.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groupfour.socialmedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    public Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);

    public Optional<User> findByCredentialsUsername(String username);

    public Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);

    public List<User> findAllByDeletedFalse();
}
