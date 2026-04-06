package com.selvaganesh7378.subtrack.data.local.room.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val isRead: Boolean = false
)