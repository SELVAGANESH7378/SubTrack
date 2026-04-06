package com.selvaganesh7378.subtrack.data.mapper

import com.selvaganesh7378.subtrack.data.local.room.remotekeys.SubscriptionRemoteKeysEntity
import com.selvaganesh7378.subtrack.data.local.room.subscription.SubscriptionEntity
import com.selvaganesh7378.subtrack.data.remote.auth.dto.UserDto
import com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar.CalendarRecordDto
import com.selvaganesh7378.subtrack.data.remote.auth.dto.calendar.CalendarResponseDto
import com.selvaganesh7378.subtrack.data.remote.auth.dto.login.LoginResponseDto
import com.selvaganesh7378.subtrack.data.remote.auth.dto.logout.LogOutResponse
import com.selvaganesh7378.subtrack.data.remote.auth.dto.register.RegisterResponse
import com.selvaganesh7378.subtrack.data.remote.profile.dto.profilefetch.ProfileResponseUserDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionSummaryDto
import com.selvaganesh7378.subtrack.data.remote.subscription.dto.SubscriptionsResponseDto
import com.selvaganesh7378.subtrack.domain.model.profile.Profile
import com.selvaganesh7378.subtrack.domain.model.auth.LogOutResult
import com.selvaganesh7378.subtrack.domain.model.auth.LoginResult
import com.selvaganesh7378.subtrack.domain.model.auth.RegisterResult
import com.selvaganesh7378.subtrack.domain.model.auth.User
import com.selvaganesh7378.subtrack.domain.model.calendar.CalendarData
import com.selvaganesh7378.subtrack.domain.model.calendar.CalendarMonthRecord
import com.selvaganesh7378.subtrack.domain.model.subscription.Subscription
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionSummary
import com.selvaganesh7378.subtrack.domain.model.subscription.SubscriptionsResult
import kotlin.collections.map


fun CalendarResponseDto.toDomain(): CalendarData {
    return CalendarData(
        records = this.records.map { it.toDomain() },
        hasNextPage = this.pagination.hasNextPage,
        currentPage = this.pagination.page,
        totalPages = this.pagination.totalPages
    )
}

fun CalendarRecordDto.toDomain(): CalendarMonthRecord {
    return CalendarMonthRecord(
        year = this.year,
        month = this.month,
        monthlyCost = this.monthlyCost,
        userCurrency = this.userCurrency,
        subscriptions = this.data.map { it.toDomain() }
    )
}
fun UserDto.toDomain(): Profile {
    val tempCreateAt = createdAt.take(4)
    return Profile(
        id = uid,
        name = this.name,
        email = this.email,
        photoUrl = img ?: "",
        timezone = timezone,
        createdAt = tempCreateAt,
        currency = currency
    )
}

fun UserDto.toUser(): User {
    val tempCreateAt = createdAt.take(4)
    return User(
        id = uid,
        name = this.name,
        email = this.email,
        url = img ?: "",
        timeZone = timezone,
        createdAt = tempCreateAt,
        currency = currency
    )
}

fun LoginResponseDto.toDomain(): LoginResult {
    return LoginResult(
        message = message,
        user = this.user.toUser(),
        accessToken = this.accessToken ?: "",
        refreshToken = this.refreshToken ?: ""
    )
}

fun LogOutResponse.toDomain(): LogOutResult {
    return LogOutResult(
        message = message
    )
}

fun RegisterResponse.toDomain(): RegisterResult {
    return RegisterResult(
        message = message,
        data = data
    )
}

fun SubscriptionsResponseDto.toDomain(): SubscriptionsResult {
    return SubscriptionsResult(
        subscriptions = this.subscriptions?.map { it.toDomain() } ?: emptyList(),
        summary = this.summary?.toDomain()
    )
}



fun ProfileResponseUserDto.toDomain(): Profile {
    val tempCreateAt = this.createdAt.take(4)
    return Profile(
        id = 0,
        name = this.name,
        email = this.email,
        photoUrl = this.img,
        timezone = this.timezone,
        createdAt = tempCreateAt,
        currency = this.currency
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
        monthlyCostUSD = this.monthlyCost ?: "$0.00"
    )
}

fun SubscriptionEntity.toDomain(): Subscription {
    return Subscription(
        id = this.id,
        serviceName = this.serviceName,
        category = this.category,
        cost = this.cost,
        status = this.status,
        nextRenewal = this.nextRenewal,
        remindMeIn = this.remindMeIn,
        billingCycle = this.billingCycle,
        paymentMethod = this.paymentMethod,
        brandColorHex = this.brandColorHex,
        currency = this.currency
    )
}

fun Subscription.toEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        id = this.id,
        serviceName = this.serviceName,
        category = this.category,
        cost = this.cost,
        status = this.status,
        nextRenewal = this.nextRenewal,
        remindMeIn = this.remindMeIn,
        billingCycle = this.billingCycle,
        paymentMethod = this.paymentMethod,
        brandColorHex = this.brandColorHex,
        currency = this.currency
    )
}