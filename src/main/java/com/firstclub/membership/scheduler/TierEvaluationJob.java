package com.firstclub.membership.scheduler;

import com.firstclub.membership.service.TierEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TierEvaluationJob {

    private static final Logger logger = LoggerFactory.getLogger(TierEvaluationJob.class);

    private final TierEvaluationService tierEvaluationService;

    public TierEvaluationJob(TierEvaluationService tierEvaluationService) {
        this.tierEvaluationService = tierEvaluationService;
    }

    /**
     * Runs daily at 01:00 to evaluate tier eligibility for all active subscriptions.
     * Schedule is configurable via application.yml using spring.task.scheduling
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void evaluateTiers() {
        logger.info("Starting tier evaluation job");
        try {
            tierEvaluationService.evaluateAllActiveSubscriptions();
            logger.info("Tier evaluation job completed successfully");
        } catch (Exception e) {
            logger.error("Tier evaluation job failed", e);
        }
    }
}