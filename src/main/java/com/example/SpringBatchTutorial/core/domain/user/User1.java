package com.example.SpringBatchTutorial.core.domain.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User1 {


    @Id
    private String email;

    private String name;
}
