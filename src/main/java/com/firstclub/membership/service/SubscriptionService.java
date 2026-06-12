package com.firstclub.membership.service;

import com.firstclub.membership.dto.SubscribeRequest;
import com.firstclub.membership.dto.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse subscribe(SubscribeRequest req);

    SubscriptionResponse getCurrentSubscription(Long userId);

    SubscriptionResponse upgradeTier(Long userId, Long targetTierId);

    SubscriptionResponse downgradeTier(Long userId, Long targetTierId);

    void cancelSubscription(Long userId);

    void expireSubscriptions();

    void autoRenewSubscriptions();
}