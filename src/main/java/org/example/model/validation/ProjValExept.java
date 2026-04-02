package org.example.model.validation;

public class ProjValExept extends IllegalArgumentException {
    public static enum ProjErrorCode {
        PROJECT_NULL,
        NAME_EMPTY,
        BUDGET_NEGATIVE,
        DATE_INVALID_RANGE,
        SPEND_EXCEEDS_BUDGET
    }
    private final ProjValExept.ProjErrorCode code;
    public ProjValExept(String message, ProjErrorCode code) {
        super(message);
        this.code = code;
    }
}
