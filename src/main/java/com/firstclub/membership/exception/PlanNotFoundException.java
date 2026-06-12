package com.firstclub.membership.exception;

public class PlanNotFoundException extends MembershipException {
    public PlanNotFoundException(String message) {
        super(message);
    }
}