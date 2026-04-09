package org.example.model.database.entity;

import org.example.model.database.entity.Person;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Employee extends Person {
    //TO DO: active and pasive status
    private int id;
    private int companyId;
    private Integer age;
    private BigDecimal salary;
    private String position;
    private OffsetDateTime hiredAt;
    private String status = "Active";
    private String department = "";

    public Employee() {}

    public Employee(int companyId, String name, String surname, String email, Integer age, BigDecimal salary, String position) {
        this.companyId = companyId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.age = age;
        this.salary = salary;
        this.position = position;
    }

    public Employee(int companyId, String name, String surname, String email, Integer age, BigDecimal salary, String position, OffsetDateTime hiredAt) {
        this.companyId = companyId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.age = age;
        this.salary = salary;
        this.position = position;
        this.hiredAt = hiredAt;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getSalary() {
        return salary;
    }
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public OffsetDateTime getHiredAt() {
        return hiredAt;
    }
    public void setHiredAt(OffsetDateTime hiredAt) {
        this.hiredAt = hiredAt;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}

