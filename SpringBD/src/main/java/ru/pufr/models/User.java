package ru.pufr.models;

import lombok.Data;

//import jakarta.persistence.*;
import javax.persistence.*;
import java.util.Optional;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)     //IDENTITY
    private Long id;

    @Column(name = "email")
    private String email;           // Мыло пользователя, большая часть регистраций и авторизаций по нему

    @Column(name = "password")
    private String password;        // пароль для входа

    @Column(name = "first_name")
    private String firstName;       // имя


    @Column(name = "last_name")
    private String lastName;        // фамилия, или второе имя

    @Column(name = "testCell")
    private String testCell;        // здесь временно хранится уникальное число, для прохождения валидации почты

    @Column(name = "pathline")
    private String pathline;        // путь к папке пользователя с личными файловым хранилищем

    @Column(name = "counterEntry")
    private Integer counterEntry;   // счётчик авторизаций пользователя

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;              // роль при авторизации (админ или пользователь)

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;          // статус активности (активный или в бане)

    public User() {
    }

    public User(String email, String password, String firstName, String lastName, String testCell, String pathline, Integer counterEntry, String role, String status) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.testCell = testCell;
        this.pathline = pathline;
        this.counterEntry = counterEntry;
        this.role = Role.valueOf(role);
        this.status = Status.valueOf(status);
        //this.role = Role.USER;
        //this.status = Status.ACTIVE;
    }
}
