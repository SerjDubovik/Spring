package ru.pufr.models;

import lombok.Data;

//import jakarta.persistence.*;
import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)     //IDENTITY
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;


    @Column(name = "last_name")
    private String lastName;

    @Column(name = "testCell")
    private String testCell;


    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public User() {
    }

    public User(String email, String password, String firstName, String lastName, String testCell, String role, String status) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.testCell = testCell;
        this.role = Role.valueOf(role);
        this.status = Status.valueOf(status);
        //this.role = Role.USER;
        //this.status = Status.ACTIVE;
    }

}
