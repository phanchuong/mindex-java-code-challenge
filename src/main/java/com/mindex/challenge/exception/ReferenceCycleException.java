package com.mindex.challenge.exception;

//Custom exception to handle situation where Reference Cycle detected in the Employee hierarchy
public class ReferenceCycleException extends Exception {
     
    
    /** 
     * This will override the default exception message and give a more detailed message
     * @return String
     */
    @Override
    public String getMessage() {
        return "Reference Cycle detected in Employee hierarchy";
    }

}
