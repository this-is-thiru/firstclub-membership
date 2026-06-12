package com.firstclub.membership.exception;

public class SubscriptionNotFoundException extends MembershipException {
    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}