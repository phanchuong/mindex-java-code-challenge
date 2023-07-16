package com.mindex.challenge.exception;

public class EmployeeNotFoundException extends Exception {

    private String employeeId;

    public EmployeeNotFoundException() {
    }

    public EmployeeNotFoundException(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * This will override the default exception message and give a more detailed
     * message
     * 
     * @return String
     */
    @Override
    public String getMessage() {
        return "No such employeeId" + (employeeId != null ? ": " + employeeId : "");
    }

}
