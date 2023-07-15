package com.mindex.challenge.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.ReferenceCycleException;
import com.mindex.challenge.service.ReportingStructureService;

//Implementation of ReportingStructureService
@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    
    /** 
     * The wrapper read method that takes employeeId and return the reporting structure
     * @param employeeId
     * @return ReportingStructure
     * @throws ReferenceCycleException
     */
    @Override
    public ReportingStructure read(String employeeId) throws ReferenceCycleException {
        LOG.debug("Reading reporting structure for employeeId [{}]", employeeId);
        return read(employeeId, new HashSet<>());
    }

    
    /**
     * The recursive method that takes employeeId and a set of supervisors' ids and
     * return the reporting structure. It uses a set of ids from supervisors to avoid
     * reference cycle that would create infinite recursion
     * 
     * @param employeeId
     * @param supervisorIds
     * @return ReportingStructure
     * @throws ReferenceCycleException
     */
    private ReportingStructure read(String employeeId, Set<String> supervisorIds) throws ReferenceCycleException {
        //First check if this employeeId appeared earlier
        if (supervisorIds.contains(employeeId))
            throw new ReferenceCycleException();
        else //If not then add it to the set
            supervisorIds.add(employeeId);

        //Find the employee
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);

        //Make sure the list of direct reports are not null
        if (employee.getDirectReports() != null) {
            List<Employee> directReports = employee.getDirectReports();
            
            //Initialize the numberOfReports by size of direct reports list
            int numberOfReports = directReports.size();
            
            //Recursively find the numberOfReports for each direct report and add them to the final numberOfReports
            for (int i = 0; i < directReports.size(); i++) {
                ReportingStructure subReportingStructure = this.read(directReports.get(i).getEmployeeId(),
                        supervisorIds);
                numberOfReports += subReportingStructure.getNumberOfReports();
            }
            reportingStructure.setNumberOfReports(numberOfReports);
        }
        return reportingStructure;
    }

}
