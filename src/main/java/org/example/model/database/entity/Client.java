package org.example.model.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Client extends Person {

    private int companyId;
    private String phone;
    private BigDecimal monthlyIncome;
    private OffsetDateTime createdAt;

    public Client(int companyId, String name, String surname, String email, String phone, BigDecimal monthlyIncome) {
        this.companyId = companyId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.monthlyIncome = monthlyIncome;
    }
}