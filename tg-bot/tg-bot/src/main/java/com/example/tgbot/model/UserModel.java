package com.example.tgbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Time;

@Data
@Entity
public class UserModel {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String nickName;

    private String city;
    private Time time;


    public UserModel(Long id, String firstName, String lastName, String nickName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        city = "Saint Petersburg";
        time = Time.valueOf("09:00:00");
    }
    public UserModel(){

    }
}
