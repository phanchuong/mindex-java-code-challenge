package com.mindex.challenge.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.service.CompensationService;
import org.springframework.data.domain.Example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    CompensationRepository compensationRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    /**
     * The service method to create a compensation from input
     * 
     * @param compensation
     * @return Compensation
     */
    @Override
    public Compensation create(Compensation compensation) throws EmployeeNotFoundException {
        LOG.debug("Creating compensation [{}]", compensation);
        // Check if employeeId valid and such employee exists
        String employeeId = null;
        if (compensation.getEmployee() != null)
            employeeId = compensation.getEmployee().getEmployeeId();
        Employee employee = null;
        if (employeeId != null)
            employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return compensationRepository.save(compensation);
    }

    /**
     * The service method to read a compensation from input employeeId
     * 
     * @param employeeId
     * @return Compensation
     */
    @Override
    public Compensation read(String employeeId) {
        Compensation compensation = compensationRepository.findByEmployeeId(employeeId);
        return compensation;
    }

}
