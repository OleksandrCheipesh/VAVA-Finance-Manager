package org.example.model.validation;

import org.example.model.database.entity.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.model.validation.EmpValExept.EmployeeErrorCode;

class EmployeeValidatorTest {

    private Employee validEmployee() {
        Employee e = new Employee();
        e.setCompanyId(1);
        e.setName("John");
        e.setSurname("Doe");
        e.setPosition("Developer");
        e.setEmail("john@doe.com");
        e.setAge(30);
        e.setSalary(new BigDecimal("3000"));
        return e;
    }

    @Test
    void valid_employee_passes() {
        assertDoesNotThrow(() -> EmployeeValidator.validate(validEmployee()));
    }

    @Test
    void null_employee_throws() {
        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(null));

        assertEquals(EmployeeErrorCode.INVALID_COMPANY_ID, ex.getCode());
    }



    @Test
    void invalid_companyId_throws() {
        Employee e = validEmployee();
        e.setCompanyId(0);

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.INVALID_COMPANY_ID, ex.getCode());
    }


    @Test
    void blank_name_throws() {
        Employee e = validEmployee();
        e.setName(" ");

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.NAME_EMPTY, ex.getCode());
    }


    @Test
    void blank_surname_throws() {
        Employee e = validEmployee();
        e.setSurname(null);

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.SURNAME_EMPTY, ex.getCode());
    }


    @Test
    void blank_position_throws() {
        Employee e = validEmployee();
        e.setPosition("  ");

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.POSITION_EMPTY, ex.getCode());
    }


    @Test
    void invalid_email_throws() {
        Employee e = validEmployee();
        e.setEmail("not-an-email");

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.INVALID_EMAIL, ex.getCode());
    }

    @Test
    void valid_email_passes() {
        Employee e = validEmployee();
        e.setEmail("a@b.com");

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }

    @Test
    void null_email_passes() {
        Employee e = validEmployee();
        e.setEmail(null);

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }

    @Test
    void blank_email_passes() {
        Employee e = validEmployee();
        e.setEmail("   ");

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }


    @Test
    void age_below_18_throws() {
        Employee e = validEmployee();
        e.setAge(17);

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.INVALID_AGE, ex.getCode());
    }

    @Test
    void age_above_100_throws() {
        Employee e = validEmployee();
        e.setAge(101);

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.INVALID_AGE, ex.getCode());
    }

    @Test
    void age_boundary_18_passes() {
        Employee e = validEmployee();
        e.setAge(18);

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }

    @Test
    void age_boundary_100_passes() {
        Employee e = validEmployee();
        e.setAge(100);

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }

    @Test
    void null_age_passes() {
        Employee e = validEmployee();
        e.setAge(null);

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }


    @Test
    void negative_salary_throws() {
        Employee e = validEmployee();
        e.setSalary(new BigDecimal("-1"));

        EmpValExept ex = assertThrows(EmpValExept.class,
                () -> EmployeeValidator.validate(e));

        assertEquals(EmployeeErrorCode.NEGATIVE_SALARY, ex.getCode());
    }

    @Test
    void zero_salary_passes() {
        Employee e = validEmployee();
        e.setSalary(BigDecimal.ZERO);

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }

    @Test
    void null_salary_passes() {
        Employee e = validEmployee();
        e.setSalary(null);

        assertDoesNotThrow(() -> EmployeeValidator.validate(e));
    }
}