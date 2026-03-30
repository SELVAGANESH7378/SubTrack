package com.selvaganesh7378.subtrack.domain.model.subscription

data class SubscriptionsResult(
    val subscriptions: List<Subscription>,
    val summary: SubscriptionSummary?
)
