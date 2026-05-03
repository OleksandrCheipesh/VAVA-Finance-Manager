package org.example.model.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class User extends Person {

    private String passwordHash;
    private Position position;
    private Integer companyId;
    private OffsetDateTime createdAt;
    private String language = "English";

    public User(String name, String surname, String email, String passwordHash, Position position, Integer companyId) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.position = position;
        this.companyId = companyId;
    }
}