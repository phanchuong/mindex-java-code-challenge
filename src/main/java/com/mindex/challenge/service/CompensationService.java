package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.exception.EmployeeNotFoundException;

//Interface for CompensationService
public interface CompensationService {
    Compensation create(Compensation compensation) throws EmployeeNotFoundException;
    Compensation read(String employeeId);
}
