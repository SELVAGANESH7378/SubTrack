package com.selvaganesh7378.subtrack.data.remote.subscription.dto

data class SubscriptionsResponseDto(
    val subscriptions: List<SubscriptionDto>? = emptyList(),
    val pagination: PaginationDto? = null,
    val summary: SubscriptionSummaryDto? = null
)