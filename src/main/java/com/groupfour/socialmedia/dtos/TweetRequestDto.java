package com.groupfour.socialmedia.dtos;

import java.util.List;

import com.groupfour.socialmedia.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TweetRequestDto {

    private CredentialsDto credentials;

    private String content;

}

