package com.selvaganesh7378.subtrack.data.mapper

import com.selvaganesh7378.subtrack.data.remote.profile.dto.ProfileDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionSummaryDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionsResponseDto
import com.selvaganesh7378.subtrack.domain.model.Profile
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionSummary
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionsResult

fun ProfileDto.toDomain(id: Int): Profile {
    return Profile(
        id = id,
        name = name,
        email = email,
        photoUrl = photoUrl,
        timezone = timezone
    )
}

fun SubscriptionsResponseDto.toDomain(): SubscriptionsResult {
    return SubscriptionsResult(
        subscriptions = this.subscriptions?.map { it.toDomain() } ?: emptyList(),
        summary = this.summary?.toDomain()
    )
}

fun SubscriptionDto.toDomain(): Subscription {
    return Subscription(
        id = this.id ?: 0,
        serviceName = this.serviceName ?: "Unknown Service",
        category = this.category ?: "Uncategorized",
        cost = this.cost ?: 0.0,
        status = this.status ?: "unknown",
        nextRenewal = this.nextRenewal ?: "",
        remindMeIn = this.remindMeIn ?: 0,
        billingCycle = this.billingCycle ?: "Monthly",
        paymentMethod = this.paymentMethod ?: "Unknown",
        brandColorHex = this.brandColorHex ?: "#808080", // Default gray
        currency = this.currency ?: "USD"
    )
}

fun SubscriptionSummaryDto.toDomain(): SubscriptionSummary {
    return SubscriptionSummary(
        totalActive = this.totalActive ?: 0,
        monthlyCostUSD = this.monthlyCostUSD ?: "$0.00"
    )
}