package org.example.model.validation;
public class EmpValExept extends IllegalArgumentException {
    public static enum EmployeeErrorCode {
        NAME_EMPTY,
        SURNAME_EMPTY,
        INVALID_EMAIL,
        INVALID_AGE,
        NEGATIVE_SALARY,
        INVALID_COMPANY_ID,
        POSITION_EMPTY
    }
    private final EmployeeErrorCode code;

    public EmpValExept(EmployeeErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public EmployeeErrorCode getCode() {
        return code;
    }
}
