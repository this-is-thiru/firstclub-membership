package com.firstclub.membership.scheduler;

import com.firstclub.membership.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoRenewalJob {

    private static final Logger logger = LoggerFactory.getLogger(AutoRenewalJob.class);

    private final SubscriptionService subscriptionService;

    public AutoRenewalJob(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Runs daily at 00:30 to auto-renew subscriptions that have auto-renew enabled.
     * Schedule is configurable via application.yml using spring.task.scheduling
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void renewMemberships() {
        logger.info("Starting auto-renewal job");
        try {
            subscriptionService.autoRenewSubscriptions();
            logger.info("Auto-renewal job completed successfully");
        } catch (Exception e) {
            logger.error("Auto-renewal job failed", e);
        }
    }
}