package com.selvaganesh7378.subtrack.di

import com.selvaganesh7378.subtrack.data.repository.AuthRepositoryImpl
import com.selvaganesh7378.subtrack.data.repository.ProfileRepositoryImpl
import com.selvaganesh7378.subtrack.domain.repository.AuthRepository
import com.selvaganesh7378.subtrack.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}