package com.groupfour.socialmedia.entities;

import java.util.List;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Hashtag {

    @Id
    @GeneratedValue
    private Long id;



}
