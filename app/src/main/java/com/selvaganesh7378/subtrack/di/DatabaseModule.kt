package com.selvaganesh7378.subtrack.di

import android.content.Context
import androidx.room.Room
import com.selvaganesh7378.subtrack.data.local.room.SubTrackDatabase
import com.selvaganesh7378.subtrack.data.local.room.SubscriptionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SubTrackDatabase {
        return Room.databaseBuilder(
            context,
            SubTrackDatabase::class.java,
            "subtrack_database"
        )
            .fallbackToDestructiveMigration() // Useful during development if schema changes
            .build()
    }

    @Provides
    @Singleton
    fun provideSubscriptionDao(database: SubTrackDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }
}