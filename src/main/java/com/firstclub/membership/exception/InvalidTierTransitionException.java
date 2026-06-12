package com.firstclub.membership.exception;

public class InvalidTierTransitionException extends MembershipException {
    public InvalidTierTransitionException(String message) {
        super(message);
    }
}