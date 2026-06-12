package com.firstclub.membership.scheduler;

import com.firstclub.membership.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MembershipExpiryJob {

    private static final Logger logger = LoggerFactory.getLogger(MembershipExpiryJob.class);

    private final SubscriptionService subscriptionService;

    public MembershipExpiryJob(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Runs daily at midnight to expire memberships that have passed their expiry date.
     * Schedule is configurable via application.yml using spring.task.scheduling
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireMemberships() {
        logger.info("Starting membership expiry job");
        try {
            subscriptionService.expireSubscriptions();
            logger.info("Membership expiry job completed successfully");
        } catch (Exception e) {
            logger.error("Membership expiry job failed", e);
        }
    }
}