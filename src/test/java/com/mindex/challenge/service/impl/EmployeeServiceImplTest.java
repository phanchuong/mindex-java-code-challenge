package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.ReferenceCycleException;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting-structure";
        compensationUrl = "http://localhost:" + port + "/employee/compensation/create";
        compensationIdUrl = "http://localhost:" + port + "/employee/{id}/compensation/read";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // Read checks
        Employee readEmployee = restTemplate
                .getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee = restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(readEmployee, headers),
                Employee.class,
                readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testReadReportingStructure() {
        // Create employees first
        Employee testEmployee1 = new Employee();
        testEmployee1.setFirstName("Onefirst");
        testEmployee1.setLastName("Onelast");
        testEmployee1.setDepartment("Engineering");
        testEmployee1.setPosition("Developer");
        Employee createdEmployee1 = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();

        Employee testEmployee2 = new Employee();
        testEmployee2.setFirstName("Twofirst");
        testEmployee2.setLastName("Twolast");
        testEmployee2.setDepartment("Engineering");
        testEmployee2.setPosition("Developer");
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee2, Employee.class).getBody();

        Employee testEmployee3 = new Employee();
        testEmployee3.setFirstName("Threefirst");
        testEmployee3.setLastName("Threelast");
        testEmployee3.setDepartment("Engineering");
        testEmployee3.setPosition("Manager");
        Employee createdEmployee3 = restTemplate.postForEntity(employeeUrl, testEmployee3, Employee.class).getBody();

        Employee testEmployee4 = new Employee();
        testEmployee4.setFirstName("Fourfirst");
        testEmployee4.setLastName("Fourlast");
        testEmployee4.setDepartment("Engineering");
        testEmployee4.setPosition("Director");
        Employee createdEmployee4 = restTemplate.postForEntity(employeeUrl, testEmployee4, Employee.class).getBody();

        Employee readEmployee4 = restTemplate
                .getForEntity(employeeIdUrl, Employee.class, createdEmployee4.getEmployeeId()).getBody();
        assertEquals(createdEmployee4.getEmployeeId(), readEmployee4.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee4, readEmployee4);

        // Then create the hierarchy
        // 4
        // / \
        // 3 2
        // /
        // 1
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        createdEmployee3.setDirectReports(Arrays.asList(createdEmployee1));
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(createdEmployee3, headers),
                Employee.class,
                createdEmployee3.getEmployeeId()).getBody();

        createdEmployee4.setDirectReports(Arrays.asList(createdEmployee2, createdEmployee3));
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(createdEmployee4, headers),
                Employee.class,
                createdEmployee4.getEmployeeId()).getBody();

        ReportingStructure reportingStructure4 = restTemplate
                .getForEntity(reportingStructureUrl, ReportingStructure.class,
                        createdEmployee4.getEmployeeId())
                .getBody();

        assertEquals(3, reportingStructure4.getNumberOfReports());

    }

    @Test
    public void testCreateReadCompensation() {
        // First create a new employee
        Employee compEmployee1 = new Employee();
        compEmployee1.setFirstName("Onecompfirst");
        compEmployee1.setLastName("Onecomplast");
        compEmployee1.setDepartment("Engineering");
        compEmployee1.setPosition("Developer");

        Employee createdCompEmployee1 = restTemplate.postForEntity(employeeUrl, compEmployee1, Employee.class)
                .getBody();

        // Then create the compensation associated with the employee
        Compensation comp1 = new Compensation();
        comp1.setEmployee(createdCompEmployee1);
        comp1.setEffectiveDate(new Date());
        comp1.setSalary(200000);

        Compensation createdComp1 = restTemplate.postForEntity(compensationUrl,
                comp1, Compensation.class)
                .getBody();

        // Read the compensation and make sure data is the same as the one created
        // earlier
        Compensation readComp1 = restTemplate
                .getForEntity(compensationIdUrl, Compensation.class, createdCompEmployee1.getEmployeeId()).getBody();
        assertCompensationEquivalence(comp1, createdComp1);
        assertCompensationEquivalence(comp1, readComp1);

    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
