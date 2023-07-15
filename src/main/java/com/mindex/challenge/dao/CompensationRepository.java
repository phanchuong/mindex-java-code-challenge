package com.mindex.challenge.dao;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.Compensation;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, Employee> {

    // Use the MongoDB query to get the compensation from input employeeId
    @Query("{ 'employee.employeeId': ?0 }")
    Compensation findByEmployeeId(String employeeId);
}
