package org.example.model.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Employee extends Person {

    private int id;
    private int companyId;
    private Integer age;
    private BigDecimal salary;
    private String position;
    private OffsetDateTime hiredAt;
    @Setter(lombok.AccessLevel.NONE)
    private String status = "ACTIVE";
    private String department = "";

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

    public void setStatus(String status) {
        this.status = status.toUpperCase();
    }
}