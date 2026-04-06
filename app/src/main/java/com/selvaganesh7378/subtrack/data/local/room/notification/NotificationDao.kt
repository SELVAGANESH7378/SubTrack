package com.selvaganesh7378.subtrack.data.local.room.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications")
    fun getNotificationsFlow(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Transaction
    suspend fun syncNotifications(newNotifications: List<NotificationEntity>) {
        val existing = getAllNotifications().associateBy { it.id }

        val merged = newNotifications.map { newNotif ->
            newNotif.copy(isRead = existing[newNotif.id]?.isRead ?: false)
        }

        clearAll()
        insertAll(merged)
    }
}