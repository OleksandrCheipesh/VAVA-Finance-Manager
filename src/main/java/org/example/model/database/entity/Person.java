package org.example.model.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Person {
    protected int id;
    protected String name;
    protected String surname;
    protected String email;
}