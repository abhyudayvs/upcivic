package com.example.civicconnect.di

import com.example.civicconnect.repository.AuthRepository
import com.example.civicconnect.repository.IssueRepository
import com.example.civicconnect.repository.PollRepository
import com.example.civicconnect.repository.impl.AuthRepositoryImpl
import com.example.civicconnect.repository.impl.IssueRepositoryImpl
import com.example.civicconnect.repository.impl.PollRepositoryImpl
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
    abstract fun bindIssueRepository(
        issueRepositoryImpl: IssueRepositoryImpl
    ): IssueRepository

    @Binds
    @Singleton
    abstract fun bindPollRepository(
        pollRepositoryImpl: PollRepositoryImpl
    ): PollRepository
} 