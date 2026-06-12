package com.firstclub.membership.exception;

public class TierNotFoundException extends MembershipException {
    public TierNotFoundException(String message) {
        super(message);
    }
}