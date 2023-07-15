package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.ReferenceCycleException;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }

    
    /**
     * This method will handle the reporting structure request and return the
     * ReportingStructure object. In case ReferenceCycleException occurs, it will return 500 error with detailed message. 
     * 
     * @param id
     * @return ResponseEntity<Object>
     */
    @GetMapping("/employee/{id}/reporting-structure")
    public ResponseEntity<Object> readReportingStructure(@PathVariable String id) {
        LOG.debug("Received employee's reporting structure request for id [{}]", id);

        try {
            return ResponseEntity.ok(reportingStructureService.read(id));
        } catch (ReferenceCycleException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());

        }
    }

    
    /**
     * This will handle the create compensation request and return the newly created compensation
     * 
     * @param compensation
     * @return ResponseEntity<Compensation>
     */
    @PostMapping("/employee/compensation/create")
    public ResponseEntity<Compensation> createCompensation(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);
        return ResponseEntity.ok(compensationService.create(compensation));
    }

    
    /** 
     * This method will handle the read compensation request and return the existing created compensation
     * @param id
     * @return ResponseEntity<Compensation>
     */
    @GetMapping("/employee/{id}/compensation/read")
    public ResponseEntity<Compensation> readCompensation(@PathVariable String id) {
        LOG.debug("Received compensation read request for id [{}]", id);

        return ResponseEntity.ok(compensationService.read(id));
    }
}
