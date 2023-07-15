package com.mindex.challenge.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
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

    /**
     * The service method to create a compensation from input
     * 
     * @param compensation
     * @return Compensation
     */
    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);
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
