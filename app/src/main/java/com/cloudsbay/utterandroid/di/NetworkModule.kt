package com.cloudsbay.utterandroid.di

import android.content.Context
import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.auth.data.api.AuthService
import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import com.cloudsbay.utterandroid.auth.data.repository.AuthRepositoryImpl
import com.cloudsbay.utterandroid.auth.data.repository.TokenRepository
import com.cloudsbay.utterandroid.auth.data.repository.TokenRepositoryImpl
import com.cloudsbay.utterandroid.network.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesAuthService(ktorClient: KtorClient, tokenDataStore: TokenDataStore): AuthService {
        return AuthService(ktorClient, tokenDataStore)
    }

    @Provides
    @Singleton
    fun providesKtorClient(@ApplicationContext context: Context): KtorClient {
        return KtorClient(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authService: AuthService, tokenDataStore: TokenDataStore): AuthRepository {
        return AuthRepositoryImpl(authService, tokenDataStore)
    }

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext context: Context): TokenDataStore {
        return TokenDataStore(context)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(tokenDataStore: TokenDataStore): TokenRepository {
        return TokenRepositoryImpl(tokenDataStore)
    }
}
