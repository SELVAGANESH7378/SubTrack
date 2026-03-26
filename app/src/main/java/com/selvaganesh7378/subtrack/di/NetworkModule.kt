package com.selvaganesh7378.subtrack.di

import com.selvaganesh7378.subtrack.data.remote.auth.AuthApi
import com.selvaganesh7378.subtrack.data.remote.auth.AuthInterceptor
import com.selvaganesh7378.subtrack.data.remote.auth.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(authInterceptor: AuthInterceptor, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(authInterceptor)
//            .authenticator(tokenAuthenticator)
//            .build()
//    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://unresplendent-magniloquently-winfred.ngrok-free.dev/")
//            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}