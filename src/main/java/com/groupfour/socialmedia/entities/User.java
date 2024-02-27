package com.groupfour.socialmedia.entities;



import java.sql.Timestamp;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name="userTable")
public class User {
	
	@Id
	private Long id;
	
	@Embedded
	private Credentials credentials;
	
	@Embedded
	private Profile profile;
	
	private Timestamp joined;
	
	private boolean deleted;
	
}
