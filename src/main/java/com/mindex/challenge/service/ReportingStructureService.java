package com.mindex.challenge.service;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.ReferenceCycleException;

//ReportingStructureService interface
public interface ReportingStructureService {
    ReportingStructure read(String employeeId) throws ReferenceCycleException;
}
