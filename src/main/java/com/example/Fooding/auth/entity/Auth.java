package com.example.Fooding.auth.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Table(name="auth")
@Entity
@Getter
@NoArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="name")
    private String name;

    @Column(name="nickName")
    private String nickName;

    @Column(name="ssNumber")
    private String ssNumber;

    @Column(name="phoneNumber")
    private String phoneNumber;

    @Column(name="salt")
    private String salt;

    @Builder
    public Auth(String email, String password, String name, String nickName, String ssNumber, String phoneNumber, String salt){
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.ssNumber = ssNumber;
        this.phoneNumber = phoneNumber;
        this.salt = salt;

    }

    public void update(String password, String name, String nickName, String ssNumber, String phoneNumber, String salt) {
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.ssNumber = ssNumber;
        this.phoneNumber = phoneNumber;
        this.salt = salt;
    }
}
