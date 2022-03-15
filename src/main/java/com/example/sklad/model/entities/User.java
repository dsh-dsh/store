package com.example.sklad.model.entities;

import com.example.sklad.model.enums.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@EqualsAndHashCode(of = "email")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    @Column(nullable = false)
    private LocalDateTime regTime;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Role role;

}
