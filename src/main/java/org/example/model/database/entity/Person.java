package org.example.model.database.entity;

/**
 * Base class for person-like entities (User, Employee, Client).
 */
public abstract class Person {
    protected int id;
    protected String name;
    protected String surname;
    protected String email;
}
